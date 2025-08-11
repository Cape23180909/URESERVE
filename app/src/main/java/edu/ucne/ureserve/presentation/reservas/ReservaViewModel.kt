package edu.ucne.ureserve.presentation.reservas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesUiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    val navigateBack = _navigateBack.asSharedFlow()
    private val _state = MutableStateFlow<ReservaListState>(ReservaListState.Loading)
    val state: StateFlow<ReservaListState> = _state

    private val _reservaciones = MutableStateFlow<List<ReservacionesDto>>(emptyList())
    val reservaciones: StateFlow<List<ReservacionesDto>> = _reservaciones

    val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReservas()
        getReservasUsuario()
    }

    fun terminarReserva(reservacionId: Int) {
        viewModelScope.launch {
            if (reservacionId <= 0) {
                Log.e("ReservaViewModel", "ID de reserva inválido: $reservacionId")
                // Mostrar un mensaje al usuario
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
                    // Mostrar un mensaje al usuario
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Error al eliminar la reserva", e)
                // Mostrar un mensaje al usuario
            }
        }
    }

    fun loadReservas() {
        viewModelScope.launch {
            _state.value = ReservaListState.Loading
            try {
                val usuario = AuthManager.currentUser
                    ?: throw Exception("Usuario no autenticado")

                val matricula = usuario.estudiante?.matricula
                    ?: throw Exception("Estudiante sin matrícula")

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
                        val todasLasReservas = result.data ?: emptyList()
                        _reservaciones.value = todasLasReservas
                        _state.update { ReservaListState.Success(todasLasReservas) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            ReservaListState.Error(result.message ?: "Error al obtener Todas las reservas")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getReservas() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val todasLasReservas = result.data ?: emptyList()

                        val reservasDeProyector = todasLasReservas.filter {
                            it.tipoReserva == 1 //proyectores
                        }

                        _state.update { ReservaListState.Success(reservasDeProyector) }
                        _reservaciones.value = reservasDeProyector
                    }

                    is Resource.Error -> {
                        _state.update {
                            ReservaListState.Error(result.message ?: "Error al obtener las reservas")
                        }
                    }

                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getCubiculoReservas() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val todasLasReservas = result.data ?: emptyList()

                        val reservasDeCubiculo = todasLasReservas.filter {
                            it.tipoReserva == 2
                        }
                        _state.update { ReservaListState.Success(reservasDeCubiculo ) }
                        _reservaciones.value = reservasDeCubiculo
                    }

                    is Resource.Error -> {
                        _state.update {
                            ReservaListState.Error(result.message ?: "Error al obtener las reservas")
                        }
                    }

                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getLaboratorioReservas() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val todasLasReservas = result.data ?: emptyList()
                        Log.d("ViewModel", "Total de reservas obtenidas: ${todasLasReservas.size}")
                        val reservasDeLaboratorio = todasLasReservas.filter {
                            it.tipoReserva == 3
                        }
                        Log.d("ViewModel", "Reservas de laboratorio filtradas: ${reservasDeLaboratorio.size}")
                        _state.update { ReservaListState.Success(reservasDeLaboratorio) }
                        _reservaciones.value = reservasDeLaboratorio
                    }
                    is Resource.Error -> {
                        Log.e("ViewModel", "Error al obtener las reservas: ${result.message}")
                        _state.update {
                            ReservaListState.Error(result.message ?: "Error al obtener las reservas")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getReservasRestaurante() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val todasLasReservas = result.data ?: emptyList()

                        val reservasFiltradas = todasLasReservas.filter {
                            it.tipoReserva == 4 ||
                                    it.tipoReserva == 5 ||
                                    it.tipoReserva == 6
                        }

                        _state.update { ReservaListState.Success(reservasFiltradas) }
                        _reservaciones.value = reservasFiltradas
                    }

                    is Resource.Error -> {
                        _state.update {
                            ReservaListState.Error(result.message ?: "Error al obtener las reservas")
                        }
                    }

                    is Resource.Loading -> {
                        _state.update { ReservaListState.Loading }
                    }
                }
            }
        }
    }

    fun getReservasUsuario() {
        viewModelScope.launch {
            try {
                _state.value = ReservaListState.Loading
                val usuario = authManager.currentUser ?: throw Exception("Usuario no autenticado")
                val matricula = usuario.estudiante?.matricula ?: throw Exception("Usuario no tiene matrícula asociada")
                val reservas = repository.getReservasByMatricula(matricula)
                _state.value = ReservaListState.Success(reservas)
            } catch (e: HttpException) {
                _state.value = ReservaListState.Error("Error de servidor: ${e.message}")
            } catch (e: IOException) {
                _state.value = ReservaListState.Error("Error de conexión: ${e.message}")
            } catch (e: Exception) {
                _state.value = ReservaListState.Error("Error al obtener reservas: ${e.message}")
            }
        }
    }

    fun refreshReservas() {
        viewModelScope.launch {
            _state.value = ReservaListState.Loading
            try {
                val usuario = authManager.currentUser ?: throw Exception("Usuario no autenticado")
                val matricula = usuario.estudiante?.matricula ?: throw Exception("Usuario no tiene matrícula asociada")
                val reservas = repository.getReservasByMatricula(matricula)
                _state.value = ReservaListState.Success(reservas)
            } catch (e: Exception) {
                _state.value = ReservaListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    sealed class ReservaListState {
        object Loading : ReservaListState()
        data class Success(val reservas: List<ReservacionesDto>) : ReservaListState()
        data class Error(val message: String) : ReservaListState()
    }
}