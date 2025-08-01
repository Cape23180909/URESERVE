package edu.ucne.ureserve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TipoCargos")
data class TipoCargoEntity(
    @PrimaryKey
    val tipoCargoId: Int = 0,
    val nombreCargo: String = ""
)
