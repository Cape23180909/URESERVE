package edu.ucne.ureserve.data.remote.dto



data class RestaurantesDto(
    val restauranteId: Int = 0,
    val fecha: String = "",
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val codigoReserva: Int = 0
)

