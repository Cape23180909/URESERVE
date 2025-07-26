package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

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
fun UsuarioDTO.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        usuarioId = usuarioId,
        nombres = nombres,
        apellidos = apellidos,
        correoInstitucional = correoInstitucional,
        clave = clave,
        estudianteId = estudianteId
    )
}

fun UsuarioEntity.toDto(): UsuarioDTO {
    return UsuarioDTO(
        usuarioId = usuarioId,
        nombres = nombres,
        apellidos = apellidos,
        correoInstitucional = correoInstitucional,
        clave = clave,
        estudianteId = estudianteId
    )
}
