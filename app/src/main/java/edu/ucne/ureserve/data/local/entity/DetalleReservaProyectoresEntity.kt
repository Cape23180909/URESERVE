package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_reserva_proyectores")
data class DetalleReservaProyectoresEntity(
    @PrimaryKey(autoGenerate = true)
    val detalleReservaProyectorId: Int = 0,
    val codigoReserva: Int,
    val idProyector: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val estado: Int,
    val proyectorId: Int? = null
)
