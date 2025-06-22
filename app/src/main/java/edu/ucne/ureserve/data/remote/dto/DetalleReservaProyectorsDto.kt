package edu.ucne.ureserve.data.remote.dto

data class DetalleReservaProyectorsDto(
    val detalleReservaProyectorId: Int = 0,
    val codigoReserva: Int = 0,
    val idProyector: Int = 0,
    val matricula: String = "",
    val fecha: String = "",
    val horario: String = "",
    val estado: Int = 0,
    val reservacion: ReservacionesDto = ReservacionesDto(),
    val proyector: ProyectoresDto = ProyectoresDto(),
    val estudiante: EstudianteDto = EstudianteDto()
)