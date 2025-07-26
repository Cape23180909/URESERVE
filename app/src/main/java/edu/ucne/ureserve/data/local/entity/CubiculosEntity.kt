package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cubiculos")
data class CubiculosEntity(
    @PrimaryKey(autoGenerate = true)
    val cubiculoId: Int = 0,
    val nombre: String,
    val disponible: Boolean
)
