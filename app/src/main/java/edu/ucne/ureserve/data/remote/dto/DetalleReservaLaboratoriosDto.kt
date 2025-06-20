package edu.ucne.ureserve.data.remote.dto

data class DetalleReservaLaboratoriosDto(
    val detalleReservaLaboratorioId: Int = 0,
    val codigoReserva: Int = 0,
    val idLaboratorio: Int = 0,
    val matricula: String = "",
    val fecha: String = "",
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val reservacion: ReservacionesDto = ReservacionesDto(),
    val laboratorio: LaboratoriosDto = LaboratoriosDto(),
    val estudiante: EstudianteDto = EstudianteDto()
)



