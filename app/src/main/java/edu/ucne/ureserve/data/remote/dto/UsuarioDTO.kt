package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDTO (
    val usuarioId: Int = 0,
    val nombres: String = "",
    val apellidos: String = "",
     val correoInstitucional: String = "",
    val clave: String = "",
    val estudianteId: Int = 0,
    val estudiante: EstudianteDto? = null
)