package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Laboratorios")
data class LaboratorioEntity(
    @PrimaryKey(autoGenerate = true)
    val laboratorioId: Int = 0,
    val nombre: String = "",
    val disponible: Boolean
)
