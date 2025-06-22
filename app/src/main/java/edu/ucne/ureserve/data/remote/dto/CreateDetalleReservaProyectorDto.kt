package edu.ucne.ureserve.data.remote.dto

data class CreateDetalleReservaProyectorDto(
    val codigoReserva: Int = 0,
    val idProyector: Int,
    val matricula: String,
    val fecha: String,      // Formato: "yyyy-MM-dd"
    val horario: String,    // Formato: "HH:mm" (ej. "08:00")
    val estado: Int = 0     // 0 por defecto (según tu modelo)
)