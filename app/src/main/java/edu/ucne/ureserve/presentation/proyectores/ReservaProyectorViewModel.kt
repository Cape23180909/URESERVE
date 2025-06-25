package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.repository.ProyectorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale // Import Locale
import javax.inject.Inject

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    private val repository: ProyectorRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ReservaProyectorState())
    val state: StateFlow<ReservaProyectorState> = _state.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerFechaActual(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

    @RequiresApi(Build.VERSION_CODES.O) // Add this annotation as we're using LocalDate/LocalTime
    fun verificarDisponibilidad(fecha: String, horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Ensure the date formatter is consistent
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).format(dateFormatter)

                // Ensure time formatter with Locale.US for both parsing and formatting
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

                // Parse and re-format horaInicio and horaFin to ensure consistency
                val horaInicioFormatted = LocalTime.parse(horaInicio, timeFormatter).format(timeFormatter)
                val horaFinFormatted = LocalTime.parse(horaFin, timeFormatter).format(timeFormatter)

                val proyectores = repository.getProyectoresDisponibles(fechaFormatted, horaInicioFormatted, horaFinFormatted)
                _state.value = _state.value.copy(
                    proyectores = proyectores,
                    isLoading = false,
                    disponibilidadVerificada = true
                )
            } catch (e: DateTimeParseException) {
                _state.value = _state.value.copy(
                    error = "Error de formato de fecha/hora al verificar disponibilidad: Asegúrate de que las fechas estén en 'dd-MM-yyyy' y las horas en 'hh:mm AM/PM'. Detalle: ${e.message}",
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al verificar disponibilidad: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearReserva(
        fecha: String, // Expected: "dd-MM-yyyy" (as per previous logic)
        horaInicio: String, // Expected from UI: "hh:mm AM/PM"
        horaFin: String,    // Expected from UI: "hh:mm AM/PM"
        proyectorId: Int,
        usuarioId: Int
    ): Boolean {
        return try {
            if (fecha.isBlank()) {
                throw IllegalArgumentException("La fecha no puede estar vacía.")
            }
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fechaLocalDate = try {
                LocalDate.parse(fecha, dateFormatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Formato de fecha inválido. Use dd-MM-yyyy. Detalle: ${e.message}")
            }

            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

            val horaInicioLocalTime = try {
                LocalTime.parse(horaInicio, timeFormatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Formato de hora inicio inválido. Use hh:mm AM/PM. Detalle: ${e.message}")
            }

            val horaFinLocalTime = try {
                LocalTime.parse(horaFin, timeFormatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Formato de hora fin inválido. Use hh:mm AM/PM. Detalle: ${e.message}")
            }

            val horarioForApi = "${horaInicioLocalTime.format(timeFormatter)} - ${horaFinLocalTime.format(timeFormatter)}"

            // The date for ProyectoresDto should be formatted as "dd-MM-yyyy" if that's what the API expects for this specific field.
            // If the API for ProyectoresDto expects "yyyy-MM-dd", then change dateFormatter pattern to "yyyy-MM-dd" or create another formatter.
            // Based on your previous code, "dd-MM-yyyy" was used, so keeping that.
            val reservaDto = ProyectoresDto(
                fecha = fechaLocalDate.format(dateFormatter),
                horario = horarioForApi,
                estado = 1,
                codigoReserva = generarCodigoReserva(),
                proyectorId = proyectorId,
                usuarioId = usuarioId
            )

            val response = repository.createDetalleReservaProyector(reservaDto)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: response.message()
                throw Exception("Error del servidor (${response.code()}): $errorBody")
            }
            true
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al crear reserva: ${e.message}")
            false
        }
    }

    private fun generarCodigoReserva(): Int {
        return (100000..999999).random()
    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
    }

    @RequiresApi(Build.VERSION_CODES.O) // Add this annotation
    suspend fun confirmarReserva(dto: ProyectoresDto): Boolean {
        // Ensure that the DTO you receive here already has the correctly formatted `horario` string.
        // The DTO's `fecha` should also be in the format the API expects.
        return try {
            val response = repository.createDetalleReservaProyector(dto)
            response.isSuccessful
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al confirmar reserva: ${e.message}")
            false
        }
    }
}

data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaActual: DetalleReservaProyectorsDto? = null
)