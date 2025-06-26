package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProyectoresDto(
    val proyectorId: Int,
    val nombre: String,
    val cantidad: Int,
    val conectividad: String
)