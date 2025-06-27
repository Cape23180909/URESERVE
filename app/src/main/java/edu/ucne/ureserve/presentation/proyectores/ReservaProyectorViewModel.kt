package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.DetalleReservaProyectorsApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.ProyectorRepository
import edu.ucne.ureserve.presentation.login.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.inject.Inject
import kotlinx.serialization.json.Json

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    private val repository: ProyectorRepository,
    private val detalleReservaApi: DetalleReservaProyectorsApi,
    private val reservaApi: ReservacionesApi
) : ViewModel() {

    // Estado unificado del ViewModel
    private val _state = MutableStateFlow(ReservaProyectorState())
    val state: StateFlow<ReservaProyectorState> = _state.asStateFlow()

    // Proyector seleccionado
    private val _proyectorSeleccionado = MutableStateFlow<ProyectoresDto?>(null)
    val proyectorSeleccionado: StateFlow<ProyectoresDto?> = _proyectorSeleccionado.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerFechaActual(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun verificarDisponibilidad(fecha: String, horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).toString() // Formato ISO

                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                val horaInicioParsed = LocalTime.parse(horaInicio, timeFormatter)
                val horaFinParsed = LocalTime.parse(horaFin, timeFormatter)

                val proyectores = repository.getProyectoresDisponibles(
                    fechaFormatted,
                    horaInicioParsed.format(DateTimeFormatter.ofPattern("HH:mm")),
                    horaFinParsed.format(DateTimeFormatter.ofPattern("HH:mm"))
                )

                _state.value = _state.value.copy(
                    proyectores = proyectores,
                    isLoading = false,
                    disponibilidadVerificada = true,
                    error = null
                )
            } catch (e: DateTimeParseException) {
                _state.value = _state.value.copy(
                    error = "Error de formato: ${e.message}",
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

    fun seleccionarProyector(proyector: ProyectoresDto) {
        _proyectorSeleccionado.value = proyector
        _state.value = _state.value.copy(
            proyectorSeleccionado = proyector
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReserva(
        fechaStr: String,
        horaInicioStr: String,
        horaFinStr: String,
        proyectorId: Int,
        codigoReserva: Int
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)

                // Convertir fecha a formato ISO (yyyy-MM-dd)
                val fechaLocalDate = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                val fechaISO = fechaLocalDate.toString() // Esto dará formato yyyy-MM-dd

                // Validar formato de horas
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                val horaInicio = LocalTime.parse(horaInicioStr, timeFormatter)
                val horaFin = LocalTime.parse(horaFinStr, timeFormatter)

                if (horaFin.isBefore(horaInicio)) {
                    throw Exception("La hora final debe ser después de la inicial")
                }

                val matricula = AuthManager.currentUser?.correoInstitucional
                    ?: throw Exception("Usuario no autenticado")

                // Crear DTO con formatos correctos
                val detalleReserva = DetalleReservaProyectorsDto(
                    detalleReservaProyectorId = 0,
                    codigoReserva = codigoReserva,
                    idProyector = proyectorId,
                    matricula = matricula,
                    fecha = fechaISO,
                    horario = "${horaInicio.format(timeFormatter)} - ${horaFin.format(timeFormatter)}",
                    estado = 1
                )

                val reservacionDto = ReservacionesDto(
                    reservacionId = 0,
                    codigoReserva = codigoReserva,
                    tipoReserva = 2, // 2 = proyector
                    cantidadEstudiantes = 0,
                    fecha = fechaISO,
                    horario = detalleReserva.horario,
                    estado = 1,
                    matricula = matricula
                )

                // Llamadas API con logging
                println("Enviando reserva: ${Json.encodeToString(reservacionDto)}")
                println("Enviando detalle: ${Json.encodeToString(detalleReserva)}")

                reservaApi.insert(reservacionDto)
                detalleReservaApi.insert(detalleReserva)

                _state.value = _state.value.copy(
                    isLoading = false,
                    reservaConfirmada = true,
                    codigoReserva = codigoReserva
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al confirmar reserva"
                )
            }
        }
    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
    }
}

// Estado unificado
data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val proyectorSeleccionado: ProyectoresDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaConfirmada: Boolean = false,
    val codigoReserva: Int? = null
)