package edu.ucne.ureserve.data.remote.dto

data class DetalleReservaCubiculosDto(
    val detalleReservaCubiculoId: Int = 0,
    val codigoReserva: Int = 0,
    val idCubiculo: Int = 0,
    val matricula: String = "",
    val fecha: String = "",
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val reservacion: ReservacionesDto = ReservacionesDto(),
    val cubiculo: CubiculosDto = CubiculosDto(),
    val estudiante: EstudianteDto = EstudianteDto()
)

