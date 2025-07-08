package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaCubiculosApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.data.repository.CubiculoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Member
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReservaCubiculoViewModel @Inject constructor(
    private val repository: CubiculoRepository,
    private val detalleReservaApi: DetalleReservaCubiculosApi,
    private val cubiculoApi: CubiculosApi,
    private val reservaApi: ReservacionesApi
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

    fun setSelectedHours(hours: String) {
        _selectedHours.value = hours
    }

    private fun addMemberIfNotExists(usuario: UsuarioDTO) {
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == usuario.usuarioId }) {
            currentMembers.add(usuario)
            _members.value = currentMembers // Reemplaza completamente la lista
            Log.d("ViewModel", "Miembro agregado: ${usuario.nombres}. Total miembros: ${_members.value.size}")
        }
    }

    fun initializeWithUser(usuario: UsuarioDTO) {
        Log.d("ViewModel", "Inicializando con usuario: ${usuario.nombres}")
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == usuario.usuarioId }) {
            currentMembers.add(usuario)
            _members.value = currentMembers
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addMember(member: UsuarioDTO) {
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == member.usuarioId }) {
            currentMembers.add(member)
            _members.value = currentMembers
            Log.d("ViewModel", "Miembro agregado: ${member.nombres}. Total miembros: ${_members.value.size}")
        }
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

    private val _cubiculos = MutableStateFlow<List<CubiculosDto>>(emptyList())
    val cubiculos: StateFlow<List<CubiculosDto>> = _cubiculos.asStateFlow()

    private val _usuarios = MutableStateFlow<List<UsuarioDTO>>(emptyList())
    val usuarios: StateFlow<List<UsuarioDTO>> = _usuarios.asStateFlow()

    init {
        loadCubiculos()
    }

    private fun loadCubiculos() {
        viewModelScope.launch {
            try {
                _cubiculos.value = cubiculoApi.getAll()
            } catch (e: Exception) {
                _cubiculos.value = emptyList()
            }
        }
    }

    private val _state = MutableStateFlow(ReservaCubiculoState())
    val state: StateFlow<ReservaCubiculoState> = _state.asStateFlow()

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
                val fechaFormatted = LocalDate.parse(fecha, dateFormatter).toString()
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
                val duracion = Duration.between(horaInicio, horaFin)
                val horarioFormateado = String.format(
                    "%02d:%02d:%02d",
                    duracion.toHours(),
                    duracion.toMinutes() % 60,
                    duracion.seconds % 60
                )
                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 2,
                    fecha = fechaFormateada,
                    horario = horarioFormateado,
                    estado = 1,
                    matricula = matricula,
                    cantidadEstudiantes = 0
                )
                val response = reservaApi.insert(reservacionDto)
                if (!response.isSuccessful) {
                    throw Exception("Error en reserva: ${response.code()}")
                }
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