package edu.ucne.ureserve.presentation.proyectores

import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto

data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val proyectorSeleccionado: ProyectoresDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaConfirmada: Boolean = false,
    val codigoReserva: Int? = null,
    val resultado: Resource<ReservacionesDto>? = null
)