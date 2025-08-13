package edu.ucne.ureserve.presentation.restaurantes

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RestaurantesViewModel @Inject constructor(
    private val restauranteRepository: RestauranteRepository,
    private val reservacionRepository: ReservacionRepository
) : ViewModel() {

    companion object {
        private const val FORMATO_FECHA = "d/M/yyyy"
        private const val MENSAJE_SIN_DATOS_PERSONALES = "No hay datos personales registrados."
        private const val METODO_PAGO_TARJETA = "Tarjeta de crédito"
        private const val MENSAJE_TARJETA_GUARDADA = "Tarjeta guardada"
        private const val MENSAJE_ERROR_GUARDAR_RESERVA = "Error al guardar la reserva."
        private const val MENSAJE_ERROR_GUARDAR_DETALLES = "Error al guardar detalles."
    }

    private val _reservaConfirmada = MutableStateFlow(false)
    val reservaConfirmada: StateFlow<Boolean> = _reservaConfirmada.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState: StateFlow<RestaurantesUiState> = _uiState.asStateFlow()

    private val _restaurantes = mutableStateOf<List<RestaurantesDto>>(emptyList())
    val restaurantes: State<List<RestaurantesDto>> = _restaurantes

    private val _reservaSeleccionada = MutableStateFlow<ReservacionesDto?>(null)
    val reservaSeleccionada: StateFlow<ReservacionesDto?> = _reservaSeleccionada.asStateFlow()

    private val _detalleReservaSeleccionada = MutableStateFlow<DetalleReservaRestaurantesDto?>(null)

    private val _disponibilidad = MutableStateFlow<DisponibilidadState>(DisponibilidadState.NotChecked)

    init {
        getRestaurantes()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        _uiState.update { it.copy(fecha = date.format(DateTimeFormatter.ofPattern(FORMATO_FECHA))) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun verificarDisponibilidad(horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _disponibilidad.value = DisponibilidadState.Loading
            try {
                val horaInicioParsed = LocalTime.parse(horaInicio)
                val horaFinParsed = LocalTime.parse(horaFin)

                if (horaFinParsed.isBefore(horaInicioParsed)) {
                    _disponibilidad.value = DisponibilidadState.Error("La hora de fin no puede ser antes de la hora de inicio")
                    return@launch
                }

                val restaurantes = _restaurantes.value
                val restaurantesDisponibles = restaurantes.filter { _ -> true }

                if (restaurantesDisponibles.isEmpty()) {
                    _disponibilidad.value = DisponibilidadState.NotAvailable("No hay restaurantes disponibles para el horario seleccionado")
                } else {
                    _disponibilidad.value = DisponibilidadState.Available(restaurantesDisponibles)
                    _uiState.update { it.copy(restaurantes = restaurantesDisponibles) }
                }
            } catch (e: Exception) {
                _disponibilidad.value = DisponibilidadState.Error("Error al verificar disponibilidad: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReservaParaModificar(reservaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reserva = reservacionRepository.getReservacion(reservaId)
                _reservaSeleccionada.value = reserva
                val detalle = reservacionRepository.getDetalleReserva(reservaId)
                _detalleReservaSeleccionada.value = detalle

                val fechaReserva = LocalDate.parse(reserva.fecha.substring(0, 10))
                val horaInicio = LocalTime.parse(reserva.horaInicio)
                val horaFin = LocalTime.parse(reserva.horaFin)

                _uiState.update {
                    it.copy(
                        restauranteId = detalle?.detalleReservaRestauranteId,
                        nombres = detalle?.nombre ?: "",
                        apellidos = detalle?.apellidos ?: "",
                        cedula = detalle?.cedula ?: "",
                        telefono = detalle?.telefono ?: "",
                        direccion = detalle?.direccion ?: "",
                        correo = detalle?.correoElectronico ?: "",
                        fecha = fechaReserva.format(DateTimeFormatter.ofPattern(FORMATO_FECHA)),
                        horaInicio = horaInicio.toString(),
                        horaFin = horaFin.toString(),
                        matricula = reserva.matricula!!,
                        isLoading = false
                    )
                }

                verificarDisponibilidad(
                    horaInicio.toString(),
                    horaFin.toString()
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar reserva: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarReservaRestauranteCompleta(
        nuevaFecha: LocalDate,
        nuevaHoraInicio: LocalTime,
        nuevaHoraFin: LocalTime,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val reservaActual = _reservaSeleccionada.value ?: throw Exception("No hay reserva seleccionada")

                if (nuevaHoraFin.isBefore(nuevaHoraInicio)) {
                    throw Exception("La hora de fin no puede ser antes de la hora de inicio")
                }

                val fechaZoned = ZonedDateTime.of(
                    nuevaFecha,
                    nuevaHoraInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                val reservaActualizada = reservaActual.copy(
                    fecha = fechaZoned,
                    horaInicio = nuevaHoraInicio.toString(),
                    horaFin = nuevaHoraFin.toString()
                )

                val response = reservacionRepository.updateReservacion(reservaActualizada)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reservaConfirmada = true,
                            mensaje = "Reserva modificada correctamente"
                        )
                    }
                    onSuccess()
                } else {
                    throw Exception("Error al actualizar reserva: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al modificar reserva: ${e.localizedMessage}"
                    )
                }
                onError(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatearFechaParaServidor(fecha: String): String {
        val parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA))
        return parsedDate.atStartOfDay(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_INSTANT)
    }

    fun getRestaurantes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            restauranteRepository.getRestaurantes().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data ?: emptyList()
                        _restaurantes.value = data
                        _uiState.update {
                            it.copy(
                                restaurantes = data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error al cargar los restaurantes",
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    data class ReservacionParams(
        val getLista: () -> List<*>,
        val getMetodoPagoSeleccionado: () -> String?,
        val getTarjetaCredito: () -> TarjetaCreditoDto?,
        val getDatosPersonales: () -> Any,
        val restauranteId: Int,
        val horaInicio: String,
        val horaFin: String,
        val fecha: String,
        val matricula: String,
        val cantidadHoras: Int,
        val miembros: List<String>,
        val tipoReserva: Int
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun procesarReserva(params: ReservacionParams) {
        viewModelScope.launch {
            try {
                if (params.getLista().isEmpty()) {
                    _uiState.update { it.copy(errorMessage = MENSAJE_SIN_DATOS_PERSONALES) }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val codigoReserva = (100000..999999).random()

                val fechaSeleccionada = if (params.fecha.isBlank()) {
                    ZonedDateTime.now(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_INSTANT)
                } else {
                    val parsedDate = LocalDate.parse(params.fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA))
                    parsedDate.atStartOfDay(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_INSTANT)
                }

                val matriculaFormateada = formatearMatricula(params.matricula)

                val reservacionDto = ReservacionesDto(
                    reservacionId = 0,
                    codigoReserva = codigoReserva,
                    tipoReserva = params.tipoReserva,
                    cantidadEstudiantes = params.miembros.size,
                    fecha = fechaSeleccionada,
                    horaInicio = params.horaInicio,
                    horaFin = params.horaFin,
                    estado = 1,
                    matricula = matriculaFormateada
                )

                val datosPersonales = params.getDatosPersonales()
                val detalleDto = crearDetalleDto(datosPersonales)

                val resultadoReserva = guardarReserva(reservacionDto)
                val resultadoDetalle = guardarDetalle(detalleDto)

                if (params.getMetodoPagoSeleccionado() == METODO_PAGO_TARJETA) {
                    procesarTarjeta { params.getTarjetaCredito() }
                }

                actualizarEstadoUI(codigoReserva, resultadoReserva, resultadoDetalle)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun crearDetalleDto(datosPersonales: Any): DetalleReservaRestaurantesDto {
        return when (datosPersonales) {
            is DatosPersonalesSalaVip -> DetalleReservaRestaurantesDto(
                nombre = datosPersonales.nombre,
                apellidos = datosPersonales.apellidos,
                cedula = datosPersonales.cedula,
                telefono = datosPersonales.telefono,
                direccion = datosPersonales.direccion,
                correoElectronico = datosPersonales.correoElectronico
            )
            is DatosPersonalesSalon -> DetalleReservaRestaurantesDto(
                nombre = datosPersonales.nombres,
                apellidos = datosPersonales.apellidos,
                cedula = datosPersonales.cedula,
                telefono = datosPersonales.telefono,
                direccion = datosPersonales.direccion,
                correoElectronico = datosPersonales.correoElectronico
            )
            is DatosPersonalesRestaurante -> DetalleReservaRestaurantesDto(
                nombre = datosPersonales.nombres,
                apellidos = datosPersonales.apellidos,
                cedula = datosPersonales.cedula,
                telefono = datosPersonales.telefono,
                direccion = datosPersonales.direccion,
                correoElectronico = datosPersonales.correoElectronico
            )
            else -> throw IllegalArgumentException("Tipo de dato no soportado: ${datosPersonales::class.java.simpleName}")
        }
    }

    private suspend fun guardarReserva(reservacionDto: ReservacionesDto): Boolean {
        return try {
            reservacionRepository.guardarReserva(reservacionDto).collect { resource ->
                when (resource) {
                    is Resource.Success -> Log.d("Reserva", "Reserva guardada: ${resource.data}")
                    is Resource.Error -> Log.e("Reserva", "Error: ${resource.message}")
                    is Resource.Loading -> {}
                }
            }
            true
        } catch (e: Exception) {
            Log.e("Reserva", "Error al guardar reserva: ${e.message}")
            false
        }
    }

    private suspend fun guardarDetalle(detalleDto: DetalleReservaRestaurantesDto): Boolean {
        return try {
            reservacionRepository.guardarDetalleRestaurante(detalleDto)
            true
        } catch (e: Exception) {
            Log.e("Reserva", "Error al guardar detalle: ${e.message}")
            false
        }
    }

    private suspend fun procesarTarjeta(getTarjetaCredito: () -> TarjetaCreditoDto?) {
        val tarjeta = getTarjetaCredito()
        if (tarjeta != null) {
            try {
                reservacionRepository.aceptarTarjeta(tarjeta)
                Log.d("Reserva", MENSAJE_TARJETA_GUARDADA)
            } catch (e: Exception) {
                Log.e("Reserva", "Error al guardar tarjeta: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al procesar tarjeta: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun actualizarEstadoUI(codigoReserva: Int, resultadoReserva: Boolean, resultadoDetalle: Boolean) {
        _uiState.update {
            when {
                !resultadoReserva -> it.copy(
                    isLoading = false,
                    errorMessage = MENSAJE_ERROR_GUARDAR_RESERVA
                )
                !resultadoDetalle -> it.copy(
                    isLoading = false,
                    errorMessage = MENSAJE_ERROR_GUARDAR_DETALLES
                )
                else -> {
                    it.copy(
                        isLoading = false,
                        reservaConfirmada = true,
                        mensaje = "Reserva #$codigoReserva creada exitosamente"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservacionSalaVIP(params: ReservacionParams) {
        procesarReserva(params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservacionSalonReuniones(params: ReservacionParams) {
        procesarReserva(params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservacionRestaurante(params: ReservacionParams) {
        procesarReserva(params)
    }

    fun formatearMatricula(matricula: String): String {
        val limpia = matricula.replace("-", "").replace(" ", "")
        return if (limpia.length == 8 && limpia.all { it.isDigit() }) {
            "${limpia.substring(0, 4)}-${limpia.substring(4)}"
        } else {
            matricula
        }
    }

    fun setNombres(value: String) = _uiState.update { it.copy(nombres = value) }
    fun setApellidos(value: String) = _uiState.update { it.copy(apellidos = value) }
    fun setCedula(value: String) = _uiState.update { it.copy(cedula = value) }
    fun setTelefono(value: String) = _uiState.update { it.copy(telefono = value) }
    fun setCorreo(value: String) = _uiState.update { it.copy(correo = value) }
    fun setDireccion(value: String) = _uiState.update { it.copy(direccion = value) }
    fun setFecha(value: String) = _uiState.update { it.copy(fecha = value) }
    fun setMatricula(value: String) = _uiState.update { it.copy(matricula = value) }

    sealed class DisponibilidadState {
        object NotChecked : DisponibilidadState()
        object Loading : DisponibilidadState()
        data class Available(val restaurantes: List<RestaurantesDto>) : DisponibilidadState()
        data class NotAvailable(val message: String) : DisponibilidadState()
        data class Error(val message: String) : DisponibilidadState()
    }
}