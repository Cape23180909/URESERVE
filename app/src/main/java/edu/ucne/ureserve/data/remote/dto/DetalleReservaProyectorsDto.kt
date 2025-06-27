package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetalleReservaProyectorsDto(
    val detalleReservaProyectorId: Int = 0,
    val codigoReserva: Int,
    val idProyector: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val estado: Int,
    val proyectorId: Int? = null
)