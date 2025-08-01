package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reservaciones")
data class ReservacionEntity(
    @PrimaryKey(autoGenerate = true)
    val reservacionId: Int = 0,
    val codigoReserva: Int,
    val tipoReserva: Int,
    val cantidadEstudiantes: Int = 0,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: Int = 0,
    val matricula: String
)
