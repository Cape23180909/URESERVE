package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TipoCargoesDto(
    val tipoCargoId: Int = 0,
    val nombreCargo: String = ""
)
