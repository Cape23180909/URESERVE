package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun parseHora(horaStr: String): LocalTime {
    // Normalizar el string de entrada
    val horaNormalizada = horaStr
        .trim()
        .uppercase(Locale.US)
        .replace(" A.M.", " AM")
        .replace(" P.M.", " PM")
        .replace(Regex("\\s+"), " ")

    // Probar múltiples formatos
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

@RequiresApi(Build.VERSION_CODES.O)
fun formatHora(hora: LocalTime): String {
    return hora.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseFecha(fechaStr: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return LocalDate.parse(fechaStr, formatter)
}