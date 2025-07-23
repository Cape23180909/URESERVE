package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetalleReservaLaboratoriosDto(
    val detalleReservaLaboratorioId: Int = 0,
    val codigoReserva: Int,
    val idLaboratorio: Int,
    val matricula: String,
    val fecha: String,
    val horario: String,
    val cantidadEstudiantes: Int = 0,
    val estado: Int,
)