package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservacionesDto(
    val reservacionId: Int = 0,
    val codigoReserva: Int,
    val tipoReserva: Int,
    val cantidadEstudiantes: Int = 0,
    val fecha: String,
    val horario: String,
    val estado: Int = 0,
    val matricula: String
)