package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservacionesDto(
    val reservacionId: Int = 0,
    val codigoReserva: Int = 0,
    val tipoReserva: Int = 0,
    val cantidadEstudiantes: Int = 0,
    val fecha: String = "", // Formato: "yyyy-MM-dd"
    val horario: String = "", // Formato: "HH:mm:ss"
    val estado: Int = 0,
    val matricula: String = ""
)