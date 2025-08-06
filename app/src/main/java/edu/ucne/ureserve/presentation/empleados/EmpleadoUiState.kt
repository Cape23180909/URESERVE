package edu.ucne.ureserve.presentation.empleados

import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto

data class EmpleadoUiState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val laboratorios: List<LaboratoriosDto> = emptyList(),
    val cubiculos: List<CubiculosDto> = emptyList(),
    val restaurantes: List<RestaurantesDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
