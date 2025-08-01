package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurantes")
data class RestauranteEntity(
    @PrimaryKey(autoGenerate = true)
    val restauranteId: Int? = 0,
    val nombre: String,
    val ubicacion: String,
    val capacidad: Int,
    val telefono: String,
    val correo: String,
    val descripcion: String
)
