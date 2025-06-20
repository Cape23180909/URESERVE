package edu.ucne.ureserve.data.remote.dto

data class ReservacionesDto(
    val reservacionId: Int = 0,
    val codigoReserva: Int = 0,
    val tipoReserva: Int = 0,
    val cantidadEstudiantes: Int = 0,
    val fecha: String = "", // o usa LocalDateTime si lo vas a parsear
    val horario: String = "",
    val estado: Int = 0,
    val matricula: String = ""
)
