package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_reserva_cubiculos")
data class DetalleReservaCubiculosEntity(
    @PrimaryKey(autoGenerate = true)
    val detalleReservaCubiculoId: Int = 0,
    val codigoReserva: Int,
    val idCubiculo: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val cantidadEstudiantes: Int = 0,
    val estado: Int
)
