package edu.ucne.ureserve.data.remote.dto

data class DetalleReservaRestaurantesDto(
    val detalleReservaRestauranteId: Int = 0,
    val codigoReserva: Int,
    val idRestaurante: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val cantidadEstudiantes: Int = 0,
    val estado: Int,
)