package edu.ucne.ureserve.presentation.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.AuthRepository
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val repository: ReservacionRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow<ReservaListState>(ReservaListState.Loading)
    val state: StateFlow<ReservaListState> = _state

    val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReservas()
        getReservasUsuario()
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

    fun getReservas() {
        viewModelScope.launch {
            repository.getReservas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val reservas = result.data ?: emptyList()
                        _state.update { ReservaListState.Success(reservas) }
                    }
                    is Resource.Error -> {
                        _state.update { ReservaListState.Error(result.message ?: "Error al obtener las reservas") }
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