package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetalleReservaCubiculosDto(
    val detalleReservaCubiculoId: Int = 0,
    val codigoReserva: Int,
    val idCubiculo: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val cantidadEstudiantes: Int = 0,
    val estado: Int,
)