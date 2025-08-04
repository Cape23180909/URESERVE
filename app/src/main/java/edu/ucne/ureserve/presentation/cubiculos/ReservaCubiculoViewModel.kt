package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.local.entity.toDto
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaCubiculosApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.data.repository.CubiculoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaCubiculosDto

@HiltViewModel
class ReservaCubiculoViewModel @Inject constructor(
    private val repository: CubiculoRepository,
    internal val detalleReservaApi: DetalleReservaCubiculosApi,
    internal val cubiculoApi: CubiculosApi,
    private val remoteDataSource: RemoteDataSource,
    internal val reservaApi: ReservacionesApi
) : ViewModel() {

    private val _selectedHours = MutableStateFlow("")
    val selectedHours: StateFlow<String> = _selectedHours.asStateFlow()

    private val _members = MutableStateFlow<List<UsuarioDTO>>(emptyList())
    val members: StateFlow<List<UsuarioDTO>> = _members.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _usuario = MutableStateFlow<UsuarioDTO?>(null)
    val usuario: StateFlow<UsuarioDTO?> = _usuario.asStateFlow()

    private val _cubiculos = MutableStateFlow<List<CubiculosDto>>(emptyList())
    val cubiculos: StateFlow<List<CubiculosDto>> = _cubiculos.asStateFlow()

    private val _state = MutableStateFlow(ReservaCubiculoState())
    val state: StateFlow<ReservaCubiculoState> = _state.asStateFlow()

    private val _cubiculoSeleccionado = MutableStateFlow<CubiculosDto?>(null)
    val cubiculoSeleccionado: StateFlow<CubiculosDto?> = _cubiculoSeleccionado.asStateFlow()

    init {
        loadCubiculos()
    }

    fun setSelectedHours(hours: String) {
        _selectedHours.value = hours
    }

    private fun addMemberIfNotExists(usuario: UsuarioDTO) {
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == usuario.usuarioId }) {
            currentMembers.add(usuario)
            _members.value = currentMembers
            Log.d("ViewModel", "Miembro agregado: ${usuario.nombres}. Total miembros: ${_members.value.size}")
        }
    }

    fun initializeWithUser(usuario: UsuarioDTO) {
        Log.d("ViewModel", "Inicializando con usuario: ${usuario.nombres}")
        addMemberIfNotExists(usuario)
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }


    fun addMember(member: UsuarioDTO) {
        addMemberIfNotExists(member)
    }

    fun buscarUsuarioPorMatricula(matricula: String, onResult: (UsuarioDTO?) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val usuario = repository.buscarUsuarioPorMatricula(matricula)
                if (usuario != null) {
                    addMember(usuario)
                    Log.d("ViewModel", "Usuario encontrado y agregado: ${usuario.nombres}")
                }
                onResult(usuario)
            } catch (e: Exception) {
                _errorMessage.value = "Error buscando usuario: ${e.message}"
                Log.e("ViewModel", "Error buscando usuario", e)
                onResult(null)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun seleccionarCubiculo(cubiculo: CubiculosDto) {
        _cubiculoSeleccionado.value = cubiculo
        _state.value = _state.value.copy(cubiculoSeleccionado = cubiculo)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReserva(reservaId: Int?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                reservaId?.let { id ->
                    // 1. Validar que el ID no sea cero o negativo
                    if (id <= 0) {
                        throw IllegalArgumentException("ID de reserva inválido")
                    }

                    // 2. Obtener reserva con manejo de errores
                    val reserva = try {
                        reservaApi.getById(id).also {
                            if (it.reservacionId == null) {
                                throw IllegalStateException("Reserva no encontrada")
                            }
                        }
                    } catch (e: Exception) {
                        throw Exception("Error al obtener reserva: ${e.message}")
                    }

                    // 3. Parsear fecha con validación
                    val fechaParseada = try {
                        LocalDate.parse(reserva.fecha.substring(0, 10))
                    } catch (e: Exception) {
                        throw Exception("Formato de fecha inválido: ${reserva.fecha}")
                    }

                    // 4. Obtener detalles de la reserva
                    val detalles = try {
                        detalleReservaApi.getAll().filter { it.codigoReserva == id }
                    } catch (e: Exception) {
                        emptyList() // Continuar aunque falle la carga de detalles
                    }

                    // 5. Cargar cubículo asociado si existe
                    val cubiculo = detalles.firstOrNull()?.let { detalle ->
                        try {
                            cubiculoApi.getById(detalle.idCubiculo)
                        } catch (e: Exception) {
                            null // Continuar aunque falle la carga del cubículo
                        }
                    }

                    // 6. Actualizar estado
                    _state.value = _state.value.copy(
                        cubiculoSeleccionado = cubiculo,
                        isLoading = false,
                        error = null
                    )

                    // 7. Verificar disponibilidad
                    verificarDisponibilidad(
                        fechaParseada.toString(),
                        reserva.horaInicio,
                        reserva.horaFin
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al cargar reserva: ${e.message}",
                    isLoading = false
                )
                Log.e("ReservaViewModel", "Error cargando reserva", e)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarReservaCubiculo(
        reservaId: Int,
        cubiculoId: Int,
        fechaLocal: LocalDate,
        horaInicio: LocalTime,
        horaFin: LocalTime,
        matricula: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                if (horaFin.isBefore(horaInicio)) {
                    _state.value = _state.value.copy(
                        error = "La hora de fin no puede ser antes de la hora de inicio",
                        isLoading = false
                    )
                    return@launch
                }

                // Formatear fecha y hora correctamente
                val fechaZoned = ZonedDateTime.of(
                    fechaLocal,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                // Obtener la reserva existente para preservar algunos valores
                val reservaExistente = reservaApi.getById(reservaId)

                val reservacionDto = ReservacionesDto(
                    reservacionId = reservaId,
                    codigoReserva = reservaExistente.codigoReserva, // Mantener el mismo código
                    tipoReserva = reservaExistente.tipoReserva, // Mantener el mismo tipo
                    fecha = fechaZoned,
                    horaInicio = horaInicio.toString(),
                    horaFin = horaFin.toString(),
                    estado = reservaExistente.estado, // Mantener el mismo estado
                    matricula = matricula,
                    cantidadEstudiantes = members.value.size
                )

                // Actualizar la reservación
                val response = remoteDataSource.updateReservacion(reservaId, reservacionDto)
                if (!response.isSuccessful) {
                    throw Exception("Error al actualizar reservación: ${response.code()}")
                }

                // Buscar y actualizar el detalle de reserva del cubículo
                val detalles = detalleReservaApi.getAll().filter {
                    it.codigoReserva == reservaId
                }

                if (detalles.isNotEmpty()) {
                    val detalleActualizado = detalles.first().copy(
                        idCubiculo = cubiculoId,
                        fecha = fechaLocal.toString(),
                        horario = "${horaInicio.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${horaFin.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        cantidadEstudiantes = members.value.size
                    )

                    // Actualizar el detalle
                    detalleReservaApi.update(
                        detalleActualizado.detalleReservaCubiculoId,
                        detalleActualizado
                    )
                } else {
                    // Crear nuevo detalle si no existe
                    val nuevoDetalle = DetalleReservaCubiculosDto(
                        codigoReserva = reservaId,
                        idCubiculo = cubiculoId,
                        matricula = matricula,
                        fecha = fechaLocal.toString(),
                        horario = "${horaInicio.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${horaFin.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        cantidadEstudiantes = members.value.size,
                        estado = 1 // Estado activo
                    )
                    detalleReservaApi.insert(nuevoDetalle)
                }

                _state.value = _state.value.copy(
                    reservaConfirmada = true,
                    codigoReserva = reservacionDto.codigoReserva,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al modificar reserva: ${e.message ?: "Desconocido"}",
                    isLoading = false
                )
                Log.e("Reserva", "Error modificando reserva", e)
            }
        }
    }

    fun getUsuarioById(id: Int) {
        viewModelScope.launch {
            try {
                val usuario = repository.getUsuarioById(id)
                _usuario.value = usuario
                Log.d("ReservaCubiculoViewModel", "Usuario cargado: ${usuario?.nombres} ${usuario?.apellidos}")
            } catch (e: Exception) {
                Log.e("Usuario", "Error al obtener usuario: ${e.message}")
                _usuario.value = null
            }
        }
    }

    internal fun loadCubiculos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                repository.getAll().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(isLoading = true, error = null)
                        }
                        is Resource.Success -> {
                            resource.data?.let { entities ->
                                _cubiculos.value = entities.map { it.toDto() }
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    error = null,
                                    cubiculos = _cubiculos.value
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(isLoading = false, error = resource.message)
                            try {
                                val apiCubiculos = cubiculoApi.getAll()
                                _cubiculos.value = apiCubiculos
                                _state.value = _state.value.copy(cubiculos = apiCubiculos)
                            } catch (ex: Exception) {
                                Log.e("ViewModel", "Error cargando cubículos desde API", ex)
                                _cubiculos.value = emptyList()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error cargando cubículos", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
                _cubiculos.value = emptyList()
            }
        }
    }


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
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).toString()
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                val horaInicioParsed = LocalTime.parse(horaInicio, timeFormatter)
                val horaFinParsed = LocalTime.parse(horaFin, timeFormatter)

                val disponibles = repository.getCubiculosDisponibles(
                    fechaFormatted,
                    horaInicioParsed.format(DateTimeFormatter.ofPattern("HH:mm")),
                    horaFinParsed.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
                _state.value = _state.value.copy(
                    cubiculos = disponibles,
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

    fun seleccionarProyector(cubiculo: CubiculosDto) {
        _cubiculoSeleccionado.value = cubiculo
        _state.value = _state.value.copy(cubiculoSeleccionado = cubiculo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservaCubiculo(
        cubiculoId: Int,
        cantidadHoras: Int,
        matricula: String,
        horaInicio: String,
        horaFin: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val codigoReserva = (100000..999999).random()
                val fecha = ZonedDateTime.now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)

                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 2,
                    cantidadEstudiantes = members.value.size,
                    fecha = fecha,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    estado = 1,
                    matricula = matricula
                )

                val response = reservaApi.insert(reservacionDto)

                if (response.isSuccessful) {
                    onSuccess(codigoReserva)
                    _state.value = _state.value.copy(reservaConfirmada = true, codigoReserva = codigoReserva)
                } else {
                    onError("Error ${response.code()} al registrar reserva")
                    _state.value = _state.value.copy(error = "Error ${response.code()} al registrar reserva")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
                _state.value = _state.value.copy(error = "Error: ${e.message}")
            }
        }
    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class ReservaCubiculoState(
    val cubiculos: List<CubiculosDto> = emptyList(),
    val cubiculoSeleccionado: CubiculosDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaConfirmada: Boolean = false,
    val codigoReserva: Int? = null
)
