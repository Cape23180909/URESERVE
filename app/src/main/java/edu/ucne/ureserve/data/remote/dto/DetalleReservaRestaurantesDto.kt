package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetalleReservaRestaurantesDto(
    val detalleReservaRestauranteId: Int = 0,
    val nombre: String,
    val apellidos: String,
    val cedula: String,
    val telefono: String,
    val direccion: String,
    val correoElectronico: String
)