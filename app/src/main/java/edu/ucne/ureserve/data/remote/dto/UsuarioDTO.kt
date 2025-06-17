package edu.ucne.ureserve.data.remote.dto

data class UsuarioDTO (
    val usuarioId: Int = 0,
    val nombres: String = "",
    val apellidos: String = "",
     val correoInstitucional: String = "",
    val clave: String = ""
)