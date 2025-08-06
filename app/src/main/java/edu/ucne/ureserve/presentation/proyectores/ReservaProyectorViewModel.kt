package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.DetalleReservaProyectorsApi
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.Resource
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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    internal val repository: ProyectorRepository,
    private val detalleReservaApi: DetalleReservaProyectorsApi,
    internal val reservaApi: ReservacionesApi,
    private val remoteDataSource: RemoteDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(ReservaProyectorState())
    val state: StateFlow<ReservaProyectorState> = _state.asStateFlow()

    private val _proyectorSeleccionado = MutableStateFlow<ProyectoresDto?>(null)
    val proyectorSeleccionado: StateFlow<ProyectoresDto?> = _proyectorSeleccionado.asStateFlow()

    suspend fun getDetallesReservaProyector(reservaId: Int): List<DetalleReservaProyectorsDto> {
        return remoteDataSource.getAllDetalleReservaProyector()
            .filter { it.codigoReserva == reservaId }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun actualizarReservaProyector(
        reservaId: Int,
        proyectorId: Int,
        fechaLocal: LocalDate,
        horaInicio: LocalTime,
        horaFin: LocalTime,
        matricula: String
    ) {
        try {
            if (horaFin.isBefore(horaInicio)) {
                throw Exception("La hora de fin no puede ser antes de la hora de inicio")
            }

            val fechaZoned = ZonedDateTime.of(
                fechaLocal,
                horaInicio,
                ZoneId.systemDefault()
            ).format(DateTimeFormatter.ISO_INSTANT)

            val reservacionDto = ReservacionesDto(
                reservacionId = reservaId,
                codigoReserva = (100000..999999).random(),
                tipoReserva = 1,
                fecha = fechaZoned,
                horaInicio = horaInicio.toString(),
                horaFin = horaFin.toString(),
                estado = 1,
                matricula = matricula,
                cantidadEstudiantes = 0
            )

            val reservacionActualizada = updateReservacion(reservaId, reservacionDto)

            val detalles = getDetallesReservaProyector(reservaId)
            if (detalles.isNotEmpty()) {
                val detalleActualizado = detalles.first().copy(
                    idProyector = proyectorId,
                    fecha = fechaLocal.toString(),
                    horario = "$horaInicio - $horaFin"
                )
                remoteDataSource.updateDetalleReservaProyector(
                    detalleActualizado.detalleReservaProyectorId,
                    detalleActualizado
                )
            }

            _state.value = _state.value.copy(
                resultado = Resource.Success(reservacionActualizada),
                reservaConfirmada = true,
                codigoReserva = reservacionActualizada.codigoReserva
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                resultado = Resource.Error("Error al modificar reserva: ${e.message ?: "Desconocido"}"),
                reservaConfirmada = false
            )
            throw e
        }
    }

    suspend fun updateReservacion(id: Int, reservacion: ReservacionesDto): ReservacionesDto {
        val response = reservaApi.update(id, reservacion)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            val errorMsg = try {
                response.errorBody()?.string() ?: "Error desconocido"
            } catch (e: Exception) {
                "Error leyendo cuerpo del error"
            }
            throw Exception("Error ${response.code()}: $errorMsg")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerFechaActual(): String =
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    @RequiresApi(Build.VERSION_CODES.O)
    fun verificarDisponibilidad(fecha: String, horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).toString()

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
        _state.value = _state.value.copy(proyectorSeleccionado = proyector)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarReservaProyector(
        reservaId: Int,
        proyectorId: Int,
        fechaLocal: LocalDate,
        horaInicio: LocalTime,
        horaFin: LocalTime,
        matricula: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(resultado = Resource.Loading())
            try {
                if (horaFin.isBefore(horaInicio)) {
                    _state.value = _state.value.copy(
                        resultado = Resource.Error("La hora de fin no puede ser antes de la hora de inicio")
                    )
                    return@launch
                }

                val fechaZoned = ZonedDateTime.of(
                    fechaLocal,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                val reservacionDto = ReservacionesDto(
                    reservacionId = reservaId,
                    codigoReserva = (100000..999999).random(),
                    tipoReserva = 1,
                    fecha = fechaZoned,
                    horaInicio = horaInicio.toString(),
                    horaFin = horaFin.toString(),
                    estado = 1,
                    matricula = matricula,
                    cantidadEstudiantes = 0
                )

                val reservacionActualizada = updateReservacion(reservaId, reservacionDto)

                val detalles = getDetallesReservaProyector(reservaId)
                if (detalles.isNotEmpty()) {
                    val detalleActualizado = detalles.first().copy(
                        idProyector = proyectorId,
                        fecha = fechaLocal.toString(),
                        horario = "$horaInicio - $horaFin"
                    )
                    remoteDataSource.updateDetalleReservaProyector(
                        detalleActualizado.detalleReservaProyectorId,
                        detalleActualizado
                    )
                }

                _state.value = _state.value.copy(
                    resultado = Resource.Success(reservacionActualizada),
                    reservaConfirmada = true,
                    codigoReserva = reservacionActualizada.codigoReserva
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    resultado = Resource.Error("Error al modificar reserva: ${e.message ?: "Desconocido"}"),
                    reservaConfirmada = false
                )
                Log.e("Reserva", "Error modificando reserva", e)
            }
        }
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

                val fechaFormateada = ZonedDateTime.of(
                    fechaLocal,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                val matriculaFinal = AuthManager.currentUser?.estudiante?.matricula
                    ?: throw Exception("Usuario sin matrícula")

                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 1,
                    fecha = fechaFormateada,
                    horaInicio = horaInicio.toString(),
                    horaFin = horaFin.toString(),
                    estado = 1,
                    matricula = matriculaFinal,
                    cantidadEstudiantes = 0
                )

                val response = reservaApi.insert(reservacionDto)

                if (!response.isSuccessful) {
                    throw Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
                }

                _state.value = _state.value.copy(
                    reservaConfirmada = true,
                    codigoReserva = codigoReserva,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error: ${e.message ?: "Desconocido"}",
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


