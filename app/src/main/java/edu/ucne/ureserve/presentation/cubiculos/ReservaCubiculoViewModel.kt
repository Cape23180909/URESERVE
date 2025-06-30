package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaCubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaProyectorsApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.repository.CubiculoRepository
import edu.ucne.ureserve.data.repository.ProyectorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class ReservaCubiculoViewModel @Inject constructor(
    private val repository: CubiculoRepository,
    private val detalleReservaApi: DetalleReservaCubiculosApi,
    private val cubiculoApi: CubiculosApi,
    private val reservaApi: ReservacionesApi
) : ViewModel() {

    private val _cubiculos = mutableStateOf<List<CubiculosDto>>(emptyList())
    val cubiculos: State<List<CubiculosDto>> = _cubiculos

    init {
        loadCubiculos()
    }

    private fun loadCubiculos() {
        viewModelScope.launch {
            try {
                _cubiculos.value = cubiculoApi.getAll()
            } catch (e: Exception) {
                // Manejo de errores
                _cubiculos.value = emptyList()
            }
        }
    }

    // Estado unificado del ViewModel
    private val _state = MutableStateFlow(ReservaCubiculoState())
    val state: StateFlow<ReservaCubiculoState> = _state.asStateFlow()

    // Cubiculo seleccionado
    private val _cubiculoSeleccionado = MutableStateFlow<CubiculosDto?>(null)
    val cubiculoSeleccionado: StateFlow<CubiculosDto?> = _cubiculoSeleccionado.asStateFlow()

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

                val cubiculos = repository.getCubiculosDisponibles(
                    fechaFormatted,
                    horaInicioParsed.format(DateTimeFormatter.ofPattern("HH:mm")),
                    horaFinParsed.format(DateTimeFormatter.ofPattern("HH:mm"))
                )

                _state.value = _state.value.copy(
                    cubiculos = cubiculos,
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

    fun seleccionarProyector(cubiculo: CubiculosDto) {
        _cubiculoSeleccionado.value = cubiculo
        _state.value = _state.value.copy(
            cubiculoSeleccionado = cubiculo
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservaProyector(
        proyectorId: Int,
        fechaLocal: LocalDate,
        horaInicio: LocalTime,
        horaFin: LocalTime,
        matricula: String
    ) {
        viewModelScope.launch {
            try {
                val codigoReserva = (100000..999999).random()

                // 1. Formatear fecha y hora según lo que espera el backend
                val fechaFormateada = ZonedDateTime.of(
                    fechaLocal,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                // 2. Calcular duración (TimeSpan) en formato HH:mm:ss
                val duracion = Duration.between(horaInicio, horaFin)
                val horarioFormateado = String.format(
                    "%02d:%02d:%02d",
                    duracion.toHours(),
                    duracion.toMinutes() % 60,
                    duracion.seconds % 60
                )

                // 3. Crear DTO para enviar (sin wrapper)
                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 2, // 1 para Cubiculos
                    fecha = fechaFormateada,
                    horario = horarioFormateado,
                    estado = 1, // 1 para confirmada
                    matricula = matricula,
                    cantidadEstudiantes = 0 // Valor por defecto
                )

                // 4. Enviar directamente el DTO a la API
                val response = reservaApi.insert(reservacionDto)

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Error en reserva: ${response.code()} - $errorBody")
                }

                // 5. Actualizar estado si fue exitoso
                _state.value = _state.value.copy(
                    reservaConfirmada = true,
                    codigoReserva = codigoReserva,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error: ${e.message ?: "Error desconocido al confirmar reserva"}",
                    reservaConfirmada = false
                )
                Log.e("Reserva", "Error confirmando reserva", e)
            }
        }
    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
    }
}

// Estado unificado
data class ReservaCubiculoState(
    val cubiculos: List<CubiculosDto> = emptyList(),
    val cubiculoSeleccionado: CubiculosDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaConfirmada: Boolean = false,
    val codigoReserva: Int? = null
)