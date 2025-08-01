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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import edu.ucne.ureserve.data.local.entity.toDto

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
