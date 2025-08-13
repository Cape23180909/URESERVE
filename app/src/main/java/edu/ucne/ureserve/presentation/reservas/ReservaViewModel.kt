package edu.ucne.ureserve.presentation.reservas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val repository: ReservacionRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _navigateBack = MutableSharedFlow<Unit>()
    private val _state = MutableStateFlow<ReservaListState>(ReservaListState.Loading)
    private val _reservaciones = MutableStateFlow<List<ReservacionesDto>>(emptyList())
    private val _uiState = MutableStateFlow(RestaurantesUiState())

    val navigateBack: SharedFlow<Unit> = _navigateBack.asSharedFlow()
    val state: StateFlow<ReservaListState> = _state.asStateFlow()
    val reservaciones: StateFlow<List<ReservacionesDto>> = _reservaciones.asStateFlow()
    val uiState: StateFlow<RestaurantesUiState> = _uiState.asStateFlow()

    companion object {
        private const val ERROR_USUARIO_NO_AUTENTICADO = "Usuario no autenticado"
        private const val ERROR_ESTUDIANTE_SIN_MATRICULA = "Usuario no tiene matrícula asociada"
        private const val ERROR_OBTENER_RESERVAS = "Error al obtener las reservas"
    }

    init {
        loadReservas()
    }

    fun terminarReserva(reservacionId: Int) {
        viewModelScope.launch {
            if (reservacionId <= 0) {
                Log.e("ReservaViewModel", "ID de reserva inválido: $reservacionId")
                return@launch
            }

            Log.d("ReservaViewModel", "Iniciando eliminación de reserva con ID $reservacionId")
            try {
                val exito = repository.deleteReservacion(reservacionId)
                if (exito) {
                    Log.d("ReservaViewModel", "Reserva eliminada con éxito")
                    _navigateBack.emit(Unit)
                } else {
                    Log.e("ReservaViewModel", "No se pudo eliminar la reserva con ID $reservacionId")
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Error al eliminar la reserva", e)
            }
        }
    }

    fun loadReservas() {
        viewModelScope.launch {
            _state.value = ReservaListState.Loading
            try {
                val usuario = authManager.currentUser
                    ?: throw Exception(ERROR_USUARIO_NO_AUTENTICADO)

                val matricula = usuario.estudiante?.matricula
                    ?: throw Exception(ERROR_ESTUDIANTE_SIN_MATRICULA)

                val reservas = repository.getReservasByMatricula(matricula)
                _state.value = ReservaListState.Success(reservas)
            } catch (e: Exception) {
                _state.value = ReservaListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAllReservations() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val reservas = result.data ?: emptyList()
                        _reservaciones.value = reservas
                        _state.update { ReservaListState.Success(reservas) }
                    }
                    is Resource.Error -> {
                        _state.update { ReservaListState.Error(result.message ?: "Error al obtener Todas las reservas") }
                    }
                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getReservas() {
        filterReservasByTipo(
            filtro = { it.tipoReserva == 1 }
        )
    }

    fun getCubiculoReservas() {
        filterReservasByTipo(
            filtro = { it.tipoReserva == 2 }
        )
    }

    fun getLaboratorioReservas() {
        filterReservasByTipo(
            filtro = { it.tipoReserva == 3 }
        )
    }

    fun getReservasRestaurante() {
        filterReservasByTipo(
            filtro = { it.tipoReserva in 4..6 }
        )
    }

    private fun filterReservasByTipo(
        filtro: (ReservacionesDto) -> Boolean
    ) {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val todasLasReservas = result.data ?: emptyList()
                        val reservasFiltradas = todasLasReservas.filter(filtro)
                        _reservaciones.value = reservasFiltradas
                        _state.update { ReservaListState.Success(reservasFiltradas) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            ReservaListState.Error(result.message ?: ERROR_OBTENER_RESERVAS)
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    suspend fun getReservasUsuario() {
        cargarReservasPorMatricula(ERROR_OBTENER_RESERVAS)
    }

    private suspend fun cargarReservasPorMatricula(mensajeError: String) {
        _state.value = ReservaListState.Loading
        try {
            val usuario = authManager.currentUser ?: throw Exception(ERROR_USUARIO_NO_AUTENTICADO)
            val matricula = usuario.estudiante?.matricula ?: throw Exception(ERROR_ESTUDIANTE_SIN_MATRICULA)
            val reservas = repository.getReservasByMatricula(matricula)
            _state.value = ReservaListState.Success(reservas)
        } catch (e: HttpException) {
            _state.value = ReservaListState.Error("Error de servidor: ${e.message}")
        } catch (e: IOException) {
            _state.value = ReservaListState.Error("Error de conexión: ${e.message}")
        } catch (e: Exception) {
            _state.value = ReservaListState.Error("${mensajeError}: ${e.message}")
        }
    }

    sealed class ReservaListState {
        object Loading : ReservaListState()
        data class Success(val reservas: List<ReservacionesDto>) : ReservaListState()
        data class Error(val message: String) : ReservaListState()
    }
}