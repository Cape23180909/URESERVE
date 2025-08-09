package edu.ucne.ureserve.presentation.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.repository.ReservacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val reservacionRepository: ReservacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteUiState())
    val uiState: StateFlow<ReporteUiState> = _uiState

    init {
        loadReservasProyectores()
    }

    fun loadReservasProyectores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isEmpty = false
            )

            try {
                reservacionRepository.getReservas().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                        is Resource.Success -> {
                            val reservasProyectores = resource.data?.filter { it.tipoReserva == 1 } ?: emptyList()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                reservas = reservasProyectores,
                                isEmpty = reservasProyectores.isEmpty(),
                                error = if (reservasProyectores.isEmpty()) "No hay reservas de proyectores" else null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = resource.message ?: "Error desconocido al cargar reservas"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error inesperado al cargar reservas"
                )
            }
        }
    }

    fun refresh() {
        loadReservasProyectores()
    }
}