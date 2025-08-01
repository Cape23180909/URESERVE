package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reportes")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true)
    val reporteId: Int = 0,
    val tipoReporte: Int = 0,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val fechaGeneracion: String = "",
    val generadoPor: String = "",
    val totalReservas: Int = 0,
    val reservasActivas: Int = 0,
    val reservasCanceladas: Int = 0
)
