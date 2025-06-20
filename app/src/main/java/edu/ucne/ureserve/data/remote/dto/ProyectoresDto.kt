package edu.ucne.ureserve.data.remote.dto

data class ProyectoresDto(
    val proyectorId: Int = 0,
    val fecha: String = "", // Puedes cambiar a LocalDateTime si lo deseas
    val horario: String = "",
    val estado: Int = 0,
    val codigoReserva: Int = 0
)
