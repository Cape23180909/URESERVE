package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantesDto(
    val restauranteId: Int? =0,
    val nombre: String,
    val ubicacion: String,
    val capacidad: Int,
    val telefono: String,
    val correo: String,
    val descripcion: String,
    val disponible: Boolean
)