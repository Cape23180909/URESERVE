package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Proyectores")
data class ProyectorEntity(
    @PrimaryKey(autoGenerate = true)
    val proyectorId: Int = 0,
    val nombre: String,
    val cantidad: Int,
    val conectividad: String
)
