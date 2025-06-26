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
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    private val repository: ProyectorRepository,
    private val detalleReservaApi: DetalleReservaProyectorsApi,
    private val reservaApi: ReservacionesApi
) : ViewModel() {

    // Estado principal del ViewModel
    private val _state = MutableStateFlow(ReservaProyectorState())
    val state: StateFlow<ReservaProyectorState> = _state.asStateFlow()

    // Estado para la reserva confirmada
    private val _reservaState = mutableStateOf(ReservaConfirmadaState())
    val reservaState = _reservaState

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
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).format(dateFormatter)

                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                val horaInicioFormatted = LocalTime.parse(horaInicio, timeFormatter).format(timeFormatter)
                val horaFinFormatted = LocalTime.parse(horaFin, timeFormatter).format(timeFormatter)

                val proyectores = repository.getProyectoresDisponibles(
                    fechaFormatted,
                    horaInicioFormatted,
                    horaFinFormatted
                )
                _state.value = _state.value.copy(
                    proyectores = proyectores,
                    isLoading = false,
                    disponibilidadVerificada = true
                )
            } catch (e: DateTimeParseException) {
                _state.value = _state.value.copy(
                    error = "Error de formato de fecha/hora: ${e.message}",
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
            reservaActual = _state.value.reservaActual?.copy(
                idProyector = proyector.proyectorId,
                proyector = proyector
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearReserva(
        fecha: String,
        horaInicio: String,
        horaFin: String,
        proyectorId: Int,
        usuarioId: Int,
        codigoReserva: Int
    ): Boolean {
        return try {
            val fechaLocalDate = DateTimeUtils.parseFecha(fecha)
            val horaInicioLocalTime = DateTimeUtils.parseHora(horaInicio)
            val horaFinLocalTime = DateTimeUtils.parseHora(horaFin)

            val proyectorFinal = repository.getProyector(proyectorId)

            val reservaDto = DetalleReservaProyectorsDto(
                detalleReservaProyectorId = 0,
                codigoReserva = codigoReserva,
                idProyector = proyectorId,
                matricula = AuthManager.currentUser?.correoInstitucional ?: "",
                fecha = DateTimeUtils.toBackendDateTimeFormat(fechaLocalDate, horaInicioLocalTime),
                horario = DateTimeUtils.createHorarioString(
                    horaInicioLocalTime,
                    Duration.between(horaInicioLocalTime, horaFinLocalTime).toHours()
                ),
                estado = 1,
                proyector = proyectorFinal
            )

            val response = repository.createDetalleReservaProyector(reservaDto)
            response.isSuccessful
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al crear reserva: ${e.message}")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReserva(
        fechaStr: String,
        horaInicioStr: String,
        horaFinStr: String,
        proyector: ProyectoresDto,
        codigoReserva: Int
    ) {
        viewModelScope.launch {
            try {
                // Convertir fecha a formato ISO con zona horaria
                val fechaIso = if (fechaStr.isNotEmpty()) {
                    LocalDate.parse(fechaStr).atStartOfDay(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                } else {
                    ""
                }

                val detalleReserva = DetalleReservaProyectorsDto(
                    detalleReservaProyectorId = 0,
                    codigoReserva = codigoReserva,
                    idProyector = proyector.proyectorId,
                    matricula = AuthManager.currentUser?.correoInstitucional ?: "",
                    fecha = fechaIso,
                    horario = "$horaInicioStr - $horaFinStr",
                    estado = 1,
                    proyector = proyector
                )

                val reservacionDto = ReservacionesDto(
                    reservacionId = 0,
                    codigoReserva = detalleReserva.codigoReserva,
                    tipoReserva = 1,
                    cantidadEstudiantes = 0,
                    fecha = fechaIso,
                    horario = detalleReserva.horario,
                    estado = detalleReserva.estado,
                    matricula = detalleReserva.matricula
                )

                // Ambos lanzarán excepción si fallan
                reservaApi.insert(reservacionDto)
                val response2 = detalleReservaApi.insert(detalleReserva)

                _reservaState.value = _reservaState.value.copy(
                    reservaConfirmada = true,
                    codigoReserva = detalleReserva.codigoReserva,
                    error = null
                )

            } catch (e: Exception) {
                _reservaState.value = _reservaState.value.copy(
                    reservaConfirmada = false,
                    error = "Error: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }

//    fun generarCodigoReserva(): Int {
//        return (100000..999999).random()
//    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
        _reservaState.value = _reservaState.value.copy(error = null)
    }
}

// State data classes
data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaActual: DetalleReservaProyectorsDto? = null
)

data class ReservaConfirmadaState(
    val reservaConfirmada: Boolean = false,
    val codigoReserva: Int = 0,
    val error: String? = null
)