package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val usuarioId: Int = 0,
    val nombres: String = "",
    val apellidos: String = "",
    val correoInstitucional: String = "",
    val clave: String = "",
    val estudianteId: Int = 0
)
