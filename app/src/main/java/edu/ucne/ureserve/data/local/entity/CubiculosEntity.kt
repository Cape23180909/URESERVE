package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.ucne.ureserve.data.remote.dto.CubiculosDto

@Entity(tableName = "cubiculos")
data class CubiculosEntity(
    @PrimaryKey(autoGenerate = true)
    val cubiculoId: Int = 0,
    val nombre: String,
    val disponible: Boolean
)
fun CubiculosEntity.toDto(): CubiculosDto {
    return CubiculosDto(
        cubiculoId = this.cubiculoId,
        nombre = this.nombre,
        disponible = this.disponible
    )
}

fun CubiculosDto.toEntity(): CubiculosEntity {
    return CubiculosEntity(
        cubiculoId = this.cubiculoId,
        nombre = this.nombre,
        disponible = this.disponible
    )
}