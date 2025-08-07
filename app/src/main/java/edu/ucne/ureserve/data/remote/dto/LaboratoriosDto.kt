package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaboratoriosDto(
    val laboratorioId: Int = 0,
    val nombre: String = "",
    val disponible: Boolean
)