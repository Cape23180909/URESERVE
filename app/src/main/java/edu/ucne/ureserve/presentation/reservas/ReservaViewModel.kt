package edu.ucne.ureserve.presentation.reservas

import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.ReservacionRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.presentation.login.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val repository: ReservacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ReservaListState>(ReservaListState.Loading)
    val state: StateFlow<ReservaListState> = _state

    init {
        loadReservas()
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

    sealed class ReservaListState {
        object Loading : ReservaListState()
        data class Success(val reservas: List<ReservacionesDto>) : ReservaListState()
        data class Error(val message: String) : ReservaListState()
    }
}