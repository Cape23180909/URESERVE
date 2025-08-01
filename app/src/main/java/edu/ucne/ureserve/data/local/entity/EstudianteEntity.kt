package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estudiantes")
data class EstudianteEntity(
    @PrimaryKey(autoGenerate = true)
    val estudianteId: Int = 0,
    val matricula: String = "",
    val facultad: String = "",
    val carrera: String = ""
)
