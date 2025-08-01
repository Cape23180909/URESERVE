package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_reserva_laboratorios")
data class DetalleReservaLaboratoriosEntity(
    @PrimaryKey(autoGenerate = true)
    val detalleReservaLaboratorioId: Int = 0,
    val codigoReserva: Int,
    val idLaboratorio: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val cantidadEstudiantes: Int = 0,
    val estado: Int
)
