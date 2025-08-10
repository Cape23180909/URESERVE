package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object DateTimeUtils {
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

    fun toBackendDateTimeFormat(fecha: LocalDate, hora: LocalTime): String {

        val zonedDateTime = ZonedDateTime.of(
            fecha,
            hora,
            ZoneId.systemDefault()
        )

        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    fun fromBackendDateTimeFormat(backendDate: String): LocalDateTime {

        return ZonedDateTime.parse(backendDate).toLocalDateTime()
    }

    fun createHorarioString(horaInicio: LocalTime, duracionHoras: Long): String {
        val horaFin = horaInicio.plusHours(duracionHoras)
        return "${formatHora(horaInicio)}-${formatHora(horaFin)}"
    }
}