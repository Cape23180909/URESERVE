package edu.ucne.ureserve.data.remote.dto

data class ReportesDto(
    val reporteId: Int = 0,
    val tipoReporte: Int = 0,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val fechaGeneracion: String = "",
    val generadoPor: String = "",
    val totalReservas: Int = 0,
    val reservasActivas: Int = 0,
    val reservasCanceladas: Int = 0
)