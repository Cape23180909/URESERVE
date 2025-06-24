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

    fun verificarDisponibilidad(fecha: String, horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val proyectores = repository.getProyectoresDisponibles(fecha, horaInicio, horaFin)
                _state.value = _state.value.copy(
                    proyectores = proyectores,
                    isLoading = false,
                    disponibilidadVerificada = true
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
        fecha: String, // Expected: "yyyy-MM-dd"
        horaInicio: String, // Expected from UI: "hh:mm AM/PM"
        horaFin: String,    // Expected from UI: "hh:mm AM/PM"
        proyectorId: Int,
        usuarioId: Int
    ): Boolean {
        return try {
            // Validate and parse the date string
            if (fecha.isBlank()) {
                throw IllegalArgumentException("La fecha no puede estar vacía.")
            }
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fechaLocalDate = try {
                LocalDate.parse(fecha, dateFormatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Formato de fecha inválido. Use dd-MM-yyyy. Detalle: ${e.message}")
            }

            // Define the time formatter with Locale.US for robustness when parsing/formatting "hh:mm AM/PM"
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US) // <-- CRITICAL: Added Locale.US

            // Parse horaInicio and horaFin from the "hh:mm AM/PM" strings received from the UI
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

            // **CRITICAL FIX HERE:**
            // Construct the 'horario' string exactly as the API expects: "hh:mm AM/PM - hh:mm AM/PM"
            val horarioForApi = "${horaInicioLocalTime.format(timeFormatter)} - ${horaFinLocalTime.format(timeFormatter)}"

            // Create the DTO using the combined 'horario' string
            val reservaDto = ProyectoresDto(
                fecha = fechaLocalDate.format(dateFormatter), // Format LocalDate back to string "yyyy-MM-dd"
                horario = horarioForApi, // This is the combined "hh:mm AM/PM - hh:mm AM/PM" string
                estado = 1,
                codigoReserva = generarCodigoReserva(),
                proyectorId = proyectorId,
                usuarioId = usuarioId
            )

            // Send to repository
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

    suspend fun confirmarReserva(dto: ProyectoresDto): Boolean {
        return try {
            val response = repository.createDetalleReservaProyector(dto)
            response.isSuccessful
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al confirmar reserva: ${e.message}")
            false
        }
    }
}

// Keep your ReservaProyectorState as is.
data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaActual: DetalleReservaProyectorsDto? = null
)