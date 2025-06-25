package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProyectoresDto(
    val proyectorId: Int = 0,
    val nombre: String = "",
    val cantidad: Int = 0,
    val conectividad: String = "", // HDMI, VGA, USB, etc.

    // Campos para relación con reservas (opcionales)
    val fecha: String = "", // Usando LocalDate para coincidir con DateTime de C#
    val horario: String ="",
    val estado: Int = 0, // 0 = Disponible, 1 = Reservado
    val codigoReserva: Int = 0,

    val usuarioId: Int = 0
)