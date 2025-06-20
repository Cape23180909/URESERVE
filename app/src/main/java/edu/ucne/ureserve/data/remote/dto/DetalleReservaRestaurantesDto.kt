package edu.ucne.ureserve.data.remote.dto

data class DetalleReservaRestaurantesDto(
    val detalleReservaRestauranteId: Int = 0,
    val codigoReserva: Int = 0,
    val idRestaurante: Int = 0,
    val matricula: String = "",
    val fecha: String = "",
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val reservacion: ReservacionesDto = ReservacionesDto(),
    val restaurante: RestaurantesDto = RestaurantesDto(),
    val estudiante: EstudianteDto = EstudianteDto()
)




