package edu.ucne.ureserve.data.remote.dto

data class ProyectoresDto(
    val proyectorId: Int = 0,
    val nombre: String = "",
    val cantidad: Int = 0,
    val conectividad: String = "", // HDMI, VGA, USB, etc.

    // Campos para relación con reservas (opcionales)
    val fecha: String? = null, // Usando LocalDate para coincidir con DateTime de C#
    val horario: String? = null,
    val estado: Int = 0, // 0 = Disponible, 1 = Reservado
    val codigoReserva: Int = 0
)