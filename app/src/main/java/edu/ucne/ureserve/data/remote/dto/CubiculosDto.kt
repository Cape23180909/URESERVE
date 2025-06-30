package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CubiculosDto(
    val cubiculoId: Int,
    val nombre: String,
    val disponible: Boolean
)