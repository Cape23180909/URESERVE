package edu.ucne.ureserve.presentation.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.repository.ReservacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val reservacionRepository: ReservacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteUiState())
    val uiState: StateFlow<ReporteUiState> = _uiState

    fun loadReservasPorTipo(tipo: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isEmpty = false,
                tipoActual = tipo
            )

            try {
                reservacionRepository.getReservas().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                        is Resource.Success -> {
                            val reservasFiltradas = resource.data?.filter { it.tipoReserva == tipo } ?: emptyList()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                reservas = reservasFiltradas,
                                isEmpty = reservasFiltradas.isEmpty(),
                                error = if (reservasFiltradas.isEmpty()) getMensajeVacio(tipo) else null,
                                tipoActual = tipo
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
    fun loadReservasPorTipoRestaurantes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isEmpty = false,
                tipoActual = 0 // usamos 0 como identificador especial
            )

            try {
                reservacionRepository.getReservas().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                        is Resource.Success -> {
                            val tiposPermitidos = listOf(4, 5, 6)
                            val reservasFiltradas = resource.data
                                ?.filter { it.tipoReserva in tiposPermitidos }
                                ?: emptyList()

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                reservas = reservasFiltradas,
                                isEmpty = reservasFiltradas.isEmpty(),
                                error = if (reservasFiltradas.isEmpty())
                                    "No hay reservas para restaurante, sala VIP o salón de reuniones"
                                else null
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


    private fun getMensajeVacio(tipo: Int): String {
        return when(tipo) {
            1 -> "No hay reservas de proyectores"
            2 -> "No hay reservas de cubículos"
            3 -> "  No hay reservas para el laboratorio "
            4 -> "No hay reservas para la sala VIP"
            5 -> "No hay reservas para el salón de reuniones"
            6 -> "No hay reservas para el restaurante"
            else -> "No hay reservas"
        }
    }

    fun refresh() {
        _uiState.value.tipoActual?.let { tipo ->
            loadReservasPorTipo(tipo)
        }
    }
}