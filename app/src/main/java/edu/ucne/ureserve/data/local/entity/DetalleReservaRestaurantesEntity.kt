package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_reserva_restaurantes")
data class DetalleReservaRestaurantesEntity(
    @PrimaryKey(autoGenerate = true)
    val detalleReservaRestauranteId: Int = 0,
    val nombre: String,
    val apellidos: String,
    val cedula: String,
    val telefono: String,
    val direccion: String,
    val correoElectronico: String
)
