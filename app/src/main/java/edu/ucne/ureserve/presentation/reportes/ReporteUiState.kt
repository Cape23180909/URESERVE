package edu.ucne.ureserve.presentation.reportes

import edu.ucne.ureserve.data.remote.dto.ReservacionesDto

data class ReporteUiState(
    val isLoading: Boolean = false,
    val reservas: List<ReservacionesDto> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false,
    val tipoActual: Int? = null
)