package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TarjetasCredito")
data class TarjetaCreditoEntity(
    @PrimaryKey(autoGenerate = true)
    val tarjetaCreditoId: Int? = 0,
    val numeroTarjeta: String,
    val nombreTitular: String,
    val fechaVencimiento: String,
    val codigoSeguridad: String
)
