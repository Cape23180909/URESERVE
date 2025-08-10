package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TarjetaCreditoDto (
    val tarjetaCreditoId: Int? = 0,
    val numeroTarjeta: String,
    val nombreTitular: String,
    val fechaVencimiento: String,
    val codigoSeguridad: String
)