package edu.ucne.ureserve.data.remote.dto

import java.time.LocalDate

data class DetalleReservaProyectorsDto(
    val detalleReservaProyectorId: Int = 0,
    val codigoReserva: Int = 0,
    val idProyector: Int = 0,
    val matricula: String = "",
    val fecha: LocalDate? = null,
    val horario: String = "", // Usar String en formato "HH:mm"
    val estado: Int = 0,

    // Relaciones
    val reservacion: ReservacionesDto? = null,
    val proyector: ProyectoresDto? = null,
    val estudiante: EstudianteDto? = null,
    val usuario: UsuarioDTO? = null
) {
    constructor(
        codigoReserva: Int,
        idProyector: Int,
        matricula: String,
        fecha: LocalDate?,
        horario: String,
        estado: Int = 0
    ) : this(
        codigoReserva = codigoReserva,
        idProyector = idProyector,
        matricula = matricula,
        fecha = fecha,
        horario = horario,
        estado = estado,
        proyector = ProyectoresDto(proyectorId = idProyector)
    )
}