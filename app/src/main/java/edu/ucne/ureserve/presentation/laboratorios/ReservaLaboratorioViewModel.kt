package edu.ucne.ureserve.presentation.laboratorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.LaboratoriosApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.data.repository.LaboratorioRepository
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
class ReservaLaboratorioViewModel @Inject constructor(
    private val repository: LaboratorioRepository,
    private val reservaApi: ReservacionesApi,
    private val laboratorioApi: LaboratoriosApi
) : ViewModel() {

    private val _members = MutableStateFlow<List<UsuarioDTO>>(emptyList())
    val members: StateFlow<List<UsuarioDTO>> = _members.asStateFlow()

    private val _selectedHours = MutableStateFlow("")
    val selectedHours: StateFlow<String> = _selectedHours.asStateFlow()

    private val _laboratorioNombre = MutableStateFlow("")
    val laboratorioNombre: StateFlow<String> = _laboratorioNombre.asStateFlow()

    private val _fechaSeleccionada = MutableStateFlow<String?>(null)
    val fechaSeleccionada: StateFlow<String?> = _fechaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _reservaSeleccionada = MutableStateFlow<ReservacionesDto?>(null)
    val reservaSeleccionada: StateFlow<ReservacionesDto?> = _reservaSeleccionada.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _laboratorioSeleccionado = MutableStateFlow<Int?>(null)
    val laboratorioSeleccionado: StateFlow<Int?> = _laboratorioSeleccionado.asStateFlow()

    private val _uiState = MutableStateFlow(ReservaLaboratorioUiState())
    val uiState: StateFlow<ReservaLaboratorioUiState> = _uiState.asStateFlow()
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReservaParaModificar(reservaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val reserva = reservaApi.getById(reservaId)

                // Parsear fecha y hora de la reserva existente
                val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                val horaInicio = LocalTime.parse(reserva.horaInicio)
                val horaFin = LocalTime.parse(reserva.horaFin)

                _uiState.update {
                    it.copy(
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
                _uiState.update { it.copy(error = "Error al cargar reserva: ${e.message}", isLoading = false) }
                Log.e("ReservaVM", "Error cargando reserva", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservaLaboratorio(
        laboratorioId: Int,
        cantidadHoras: Int,
        horaInicio: String,
        horaFin: String,
        fecha: String,
        matricula: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val codigoReserva = (100000..999999).random()
                val fechaSeleccionada = fecha
                    ?: ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT)
                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 3,
                    cantidadEstudiantes = members.value.size,
                    fecha = fechaSeleccionada,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    estado = 1,
                    matricula = matricula
                )
                val response = reservaApi.insert(reservacionDto)
                if (response.isSuccessful) {
                    onSuccess(codigoReserva)
                } else {
                    onError("Error ${response.code()} al registrar reserva")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarReservaLaboratorio(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val state = uiState.value
                if (state.reservaId == null) {
                    throw Exception("ID de reserva no disponible")
                }

                val fecha = state.fecha ?: throw Exception("Seleccione una fecha")
                val horaInicio = state.horaInicio ?: throw Exception("Seleccione hora de inicio")
                val horaFin = state.horaFin ?: throw Exception("Seleccione hora de fin")

                if (horaFin.isBefore(horaInicio)) {
                    throw Exception("La hora de fin no puede ser antes de la hora de inicio")
                }


                val fechaZoned = ZonedDateTime.of(
                    fecha,
                    horaInicio,
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_INSTANT)

                val reservacionDto = ReservacionesDto(
                    reservacionId = state.reservaId,
                    codigoReserva = state.codigoReserva ?: (100000..999999).random(),
                    tipoReserva = 3,
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
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun initializeWithUser(usuario: UsuarioDTO) {
        _uiState.update { state ->
            if (state.miembros.none { it.usuarioId == usuario.usuarioId }) {
                state.copy(
                    miembros = listOf(usuario) + state.miembros,
                    cantidadEstudiantes = state.miembros.size + 1
                )
            } else {
                state
            }
        }
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == usuario.usuarioId }) {
            _members.value = listOf(usuario) + currentMembers
        }
    }

    fun buscarUsuarioPorMatricula(matricula: String, onResult: (UsuarioDTO?) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val usuario = repository.buscarUsuarioPorMatricula(matricula.trim())
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

    fun addMember(member: UsuarioDTO) {
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == member.usuarioId }) {
            currentMembers.add(member)
            _members.value = currentMembers
            Log.d("ViewModel", "Miembro agregado: ${member.nombres}. Total miembros: ${_members.value.size}")
        }
    }

    fun eliminarMiembroPorMatricula(matricula: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val usuario = uiState.value.miembros.firstOrNull {
                    it.estudiante?.matricula == matricula
                }

                usuario?.let {
                    _uiState.update { state ->
                        state.copy(
                            miembros = state.miembros - it,
                            cantidadEstudiantes = state.miembros.size - 1
                        )
                    }
                    Log.d("ViewModel", "Miembro eliminado: ${it.nombres}")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar miembro: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFechaSeleccionada(fecha: LocalDate) {
        _uiState.update {
            it.copy(
                fecha = fecha,
                fechaString = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setHorario(horaInicio: LocalTime, horaFin: LocalTime) {
        _uiState.update {
            it.copy(
                horaInicio = horaInicio,
                horaFin = horaFin,
                horaInicioString = horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                horaFinString = horaFin.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun getLaboratorioNombreById(id: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val laboratorio = laboratorioApi.getById(id)
                _uiState.update {
                    it.copy(
                        laboratorioId = laboratorio.laboratorioId,
                        laboratorioNombre = laboratorio.nombre,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        laboratorioNombre = "Desconocido",
                        isLoading = false,
                        error = "Error al cargar laboratorio: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}