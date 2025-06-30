package edu.ucne.ureserve.presentation.cubiculos


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
object DateTimeUtils {
    // 1. Funciones originales (se mantienen igual para compatibilidad)
    fun parseHora(horaStr: String): LocalTime {
        val horaNormalizada = horaStr
            .trim()
            .uppercase(Locale.US)
            .replace(" A.M.", " AM")
            .replace(" P.M.", " PM")
            .replace(Regex("\\s+"), " ")

        val formatos = listOf(
            DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
            DateTimeFormatter.ofPattern("h:mm a", Locale.US),
            DateTimeFormatter.ofPattern("HH:mm", Locale.US)
        )

        for (formato in formatos) {
            try {
                return LocalTime.parse(horaNormalizada, formato)
            } catch (e: DateTimeParseException) {
                continue
            }
        }
        throw DateTimeParseException(
            "Formato de hora no válido. Use formato como: 09:00 AM o 14:00",
            horaStr,
            0
        )
    }

    fun formatHora(hora: LocalTime): String {
        return hora.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
    }

    fun parseFecha(fechaStr: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return LocalDate.parse(fechaStr, formatter)
    }

    // 2. Nuevas funciones específicas para el flujo de reservas (solución al error OffsetSeconds)
    fun toBackendDateTimeFormat(fecha: LocalDate, hora: LocalTime): String {
        // Combina fecha y hora con la zona horaria del sistema
        val zonedDateTime = ZonedDateTime.of(
            fecha,
            hora,
            ZoneId.systemDefault()
        )
        // Formato ISO-8601 completo con offset: "2025-06-26T14:30:00-04:00"
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    fun fromBackendDateTimeFormat(backendDate: String): LocalDateTime {
        // Parsea el formato ISO-8601 del backend
        return ZonedDateTime.parse(backendDate).toLocalDateTime()
    }

    // Función adicional para generar el string de horario (ej: "14:00-16:00")
    fun createHorarioString(horaInicio: LocalTime, duracionHoras: Long): String {
        val horaFin = horaInicio.plusHours(duracionHoras)
        return "${formatHora(horaInicio)}-${formatHora(horaFin)}"
    }
}