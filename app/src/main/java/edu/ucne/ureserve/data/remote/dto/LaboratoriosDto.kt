package edu.ucne.ureserve.data.remote.dto

data class LaboratoriosDto(
    val laboratorioId: Int = 0,
    val fecha: String = "", // Puedes usar LocalDateTime si prefieres
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val codigoReserva: Int = 0
)
