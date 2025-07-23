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
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import java.time.format.DateTimeFormatter

@HiltViewModel
class RestaurantesViewModel @Inject constructor(
    private val restauranteRepository: RestauranteRepository,
    private val reservacionRepository: ReservacionRepository
) : ViewModel() {

    val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState = _uiState.asStateFlow()

    private val _reservaConfirmada = MutableStateFlow(false)
    val reservaConfirmada = _reservaConfirmada.asStateFlow()

    private val _restaurantes = mutableStateOf<List<RestaurantesDto>>(emptyList())
    val restaurantes: State<List<RestaurantesDto>> = _restaurantes

    init {
        getRestaurantes()
    }

    fun validarCampos(): Boolean {
        val state = _uiState.value
        return when {
            state.nombres.isBlank() -> {
                _uiState.update { it.copy(inputError = "Los nombres no pueden estar vacíos") }
                false
            }
            state.apellidos.isBlank() -> {
                _uiState.update { it.copy(inputError = "Los apellidos no pueden estar vacíos") }
                false
            }
            state.telefono.isBlank() -> {
                _uiState.update { it.copy(inputError = "El teléfono no puede estar vacío") }
                false
            }
            state.correo.isBlank() -> {
                _uiState.update { it.copy(inputError = "El correo no puede estar vacío") }
                false
            }
            state.cedula.isBlank() -> {
                _uiState.update { it.copy(inputError = "La cédula no puede estar vacía") }
                false
            }
            state.fecha.isBlank() -> {
                _uiState.update { it.copy(inputError = "La fecha no puede estar vacía") }
                false
            }
            state.horaInicio.isBlank() || state.horaFin.isBlank() -> {
                _uiState.update { it.copy(inputError = "La hora de inicio y fin no pueden estar vacías") }
                false
            }
            else -> {
                _uiState.update { it.copy(inputError = null) }
                true
            }
        }
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

    fun crearReservacionDesdeRestaurante(
        fecha: String,
        matricula: String,
        horaInicio: String,
        horaFin: String
    ) {
        val codigoReserva = (100000..999999).random()
        val fechaConHora = "${fecha}T12:00:00"

        val nuevaReservacion = ReservacionesDto(
            codigoReserva = codigoReserva,
            tipoReserva = 4,
            cantidadEstudiantes = 0,
            fecha = fechaConHora,
            horaInicio = horaInicio,
            horaFin = horaFin,
            estado = 1,
            matricula = matricula
        )

        viewModelScope.launch {
            try {
                reservacionRepository.createReservacion(nuevaReservacion)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al crear reservación: ${e.localizedMessage}")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservacionRestaurante(
        restauranteId: Int,
        horaInicio: String,
        horaFin: String,
        fecha: String,
        matricula: String,
        cantidadHoras: Int,
        miembros: List<String>
    ) {
        viewModelScope.launch {
            try {
                if (DatosPersonalesSalaVipStore.lista.isEmpty()) {
                    _uiState.update { it.copy(errorMessage = "No hay datos personales registrados.") }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val codigoReserva = (100000..999999).random()

                val fechaSeleccionada = if (fecha.isBlank()) {
                    ZonedDateTime.now(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_INSTANT)
                } else {
                    val parsedDate = LocalDate.parse(
                        fecha,
                        DateTimeFormatter.ofPattern("d/M/yyyy") // formato de entrada
                    )
                    parsedDate.atStartOfDay(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_INSTANT)
                }

                val reservacionDto = ReservacionesDto(
                    reservacionId = 0,
                    codigoReserva = codigoReserva,
                    tipoReserva = 4,
                    cantidadEstudiantes = miembros.size,
                    fecha = fechaSeleccionada,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    estado = 1,
                    matricula = matricula
                )

                val detalleDto = DetalleReservaRestaurantesDto(
                    nombre = DatosPersonalesSalaVipStore.lista.first().nombre,
                    apellidos = DatosPersonalesSalaVipStore.lista.first().apellidos,
                    cedula = DatosPersonalesSalaVipStore.lista.first().cedula,
                    telefono = DatosPersonalesSalaVipStore.lista.first().telefono,
                    direccion = DatosPersonalesSalaVipStore.lista.first().direccion,
                    correoElectronico = DatosPersonalesSalaVipStore.lista.first().correoElectronico
                )

                val resultadoReserva = try {
                    reservacionRepository.guardarReserva(reservacionDto).collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                // Reserva guardada con éxito
                                Log.d("Reserva", "Reserva guardada: ${resource.data}")
                            }
                            is Resource.Error -> {
                                Log.e("Reserva", "Error al guardar: ${resource.message}")
                            }
                            is Resource.Loading -> {
                                // Opcional: mostrar loading
                            }
                        }
                    }
                    true
                } catch (e: Exception) {
                    Log.e("Reserva", "Error al guardar reserva: ${e.message}")
                    false
                }

                val resultadoDetalle = try {
                    reservacionRepository.guardarDetalleRestaurante(detalleDto)
                    true
                } catch (e: Exception) {
                    Log.e("Reserva", "Error al guardar detalle: ${e.message}")
                    false
                }

                _uiState.update {
                    when {
                        !resultadoReserva -> it.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar la reserva principal. Intente nuevamente."
                        )
                        !resultadoDetalle -> it.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar los detalles. Contacte al soporte."
                        )
                        else -> {
                            limpiarDatosAlmacenados()
                            it.copy(
                                isLoading = false,
                                reservaConfirmada = true,
                                mensaje = "Reserva #$codigoReserva creada exitosamente",
                                errorMessage = null
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.localizedMessage ?: "Por favor intente más tarde"}"
                    )
                }
            }
        }
    }

    // Función auxiliar para crear el DTO
    private fun crearReservacionDto(persona: DatosPersonalesSalaVip): ReservacionesDto {
        return ReservacionesDto(
            codigoReserva = (100000..999999).random(),
            tipoReserva = 4,  // Tipo para Sala VIP
            cantidadEstudiantes = persona.capacidad,
            fecha = "${persona.fecha}T12:00:00",
            horaInicio = persona.horaInicio,
            horaFin = persona.horaFin,
            estado = 1,       // 1 = Activa
            matricula = persona.matricula,
            // Asegúrate de incluir todos los campos requeridos
            reservacionId = 0 // Temporal, será asignado por el servidor
        )
    }

    // Función auxiliar para limpiar datos
    private fun limpiarDatosAlmacenados() {
        DatosPersonalesSalaVipStore.lista.clear()
        DatosPersonalesSalaVipStore.metodoPagoSeleccionado = null
    }

    // SETTERS ACTUALIZADOS
    fun setNombres(value: String) = _uiState.update { it.copy(nombres = value) }
    fun setApellidos(value: String) = _uiState.update { it.copy(apellidos = value) }
    fun setCedula(value: String) = _uiState.update { it.copy(cedula = value) }
    fun setTelefono(value: String) = _uiState.update { it.copy(telefono = value) }
    fun setCorreo(value: String) = _uiState.update { it.copy(correo = value) }
    fun setDireccion(value: String) = _uiState.update { it.copy(direccion = value) }
    fun setRestauranteId(value: Int) = _uiState.update { it.copy(restauranteId = value) }
    fun setFecha(value: String) = _uiState.update { it.copy(fecha = value) }
    fun setMatricula(value: String) = _uiState.update { it.copy(matricula = value) }
    fun setHoraInicio(value: String) = _uiState.update { it.copy(horaInicio = value) }
    fun setHoraFin(value: String) = _uiState.update { it.copy(horaFin = value) }
    fun setMetodoPago(metodo: String) = _uiState.update { it.copy(metodoPagoSeleccionado = metodo) }

    fun limpiarCampos() {
        _uiState.update {
            it.copy(
                restauranteId = null,
                nombres = "",
                apellidos = "",
                telefono = "",
                correo = "",
                cedula = "",
                direccion = "",
                matricula = "",
                fecha = "",
                horaInicio = "",
                horaFin = "",
                metodoPagoSeleccionado = null,
                inputError = null
            )
        }
    }
}

// UI STATE ACTUALIZADO
data class RestaurantesUiState(
    val restauranteId: Int? = null,
    val correo: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val matricula: String = "",
    val cedula: String = "",
    val direccion: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val metodoPagoSeleccionado: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val reservaConfirmada: Boolean = false,
    val mensaje: String? = null,
    val restaurantes: List<RestaurantesDto> = emptyList()
)