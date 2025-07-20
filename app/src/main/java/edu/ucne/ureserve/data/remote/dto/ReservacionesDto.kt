package edu.ucne.ureserve.data.remote.dto

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class ReservacionesDto(
    val reservacionId: Int = 0,
    val codigoReserva: Int,
    val tipoReserva: Int,
    val cantidadEstudiantes: Int = 0,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: Int = 0,
    val matricula: String
){
    // Propiedad computada para el formato de fecha
    val fechaFormateada: String
        get() = fecha.formatDate()

    // Función de extensión para formatear la fecha
    private fun String.formatDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault()) // Formato: "19 julio"
            val date = inputFormat.parse(this) ?: return this
            outputFormat.format(date)
        } catch (e: Exception) {
            this // Si falla, devuelve la fecha original
        }
    }
}