package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.local.entity.toDto
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.data.repository.CubiculoRepository
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
class ReservaCubiculoViewModel @Inject constructor(
    private val repository: CubiculoRepository,
    internal val cubiculoApi: CubiculosApi,
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReservaParaModificar(reservaId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val reserva = reservaApi.getById(reservaId)
                val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                val horaInicio = LocalTime.parse(reserva.horaInicio)
                val horaFin = LocalTime.parse(reserva.horaFin)
                _state.update { currentState ->
                    currentState.copy(
                        reservaId = reserva.reservacionId,
                        codigoReserva = reserva.codigoReserva,
                        cantidadEstudiantes = reserva.cantidadEstudiantes,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        estado = reserva.estado,
                        matricula = reserva.matricula,
                        fechaString = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        horaInicioString = horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                        horaFinString = horaFin.format(DateTimeFormatter.ofPattern("HH:mm")),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al cargar reserva: ${e.message}", isLoading = false) }
                Log.e("ReservaVM", "Error cargando reserva", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarReservaCubiculo(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val state = _state.value
                if (state.reservaId == null) {
                    throw Exception("ID de reserva no disponible")
                }
                val fecha = state.fecha ?: throw Exception("Seleccione una fecha")
                val horaInicio = state.horaInicio ?: throw Exception("Seleccione hora de inicio")
                val horaFin = state.horaFin ?: throw Exception("Seleccione hora de fin")
                val fechaZoned = ZonedDateTime.of(
                    fecha,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)
                val reservacionDto = ReservacionesDto(
                    reservacionId = state.reservaId,
                    codigoReserva = state.codigoReserva ?: (100000..999999).random(),
                    tipoReserva = 2,
                    fecha = fechaZoned,
                    horaInicio = horaInicio.toString(),
                    horaFin = horaFin.toString(),
                    estado = 1,
                    matricula = state.matricula,
                    cantidadEstudiantes = state.miembros.size
                )
                val response = reservaApi.update(state.reservaId, reservacionDto)
                if (!response.isSuccessful) {
                    throw Exception("Error ${response.code()} al actualizar reserva")
                }
                onSuccess()
            } catch (e: Exception) {
                onError("Error al modificar reserva: ${e.message ?: "Error desconocido"}")
                Log.e("ReservaVM", "Error modificando reserva", e)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFechaSeleccionada(fecha: LocalDate) {
        _state.update {
            it.copy(
                fecha = fecha,
                fechaString = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setHorario(horaInicio: LocalTime, horaFin: LocalTime) {
        _state.update {
            it.copy(
                horaInicio = horaInicio,
                horaFin = horaFin,
                horaInicioString = horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                horaFinString = horaFin.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
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
    fun verificarDisponibilidad() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {


                val disponibles = repository.getCubiculosDisponibles()
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
}