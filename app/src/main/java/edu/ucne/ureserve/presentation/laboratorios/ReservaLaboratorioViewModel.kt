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
import kotlinx.coroutines.launch
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
    val laboratorioNombre: StateFlow<String> = _laboratorioNombre

    private val _fechaSeleccionada = MutableStateFlow<String?>(null)
    val fechaSeleccionada: StateFlow<String?> = _fechaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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
                val fechaSeleccionada = fecha // Usa el parámetro fecha que se pasa a la función
                    ?: ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT)
                val horario = String.format("%02d:00:00", cantidadHoras)
                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 3, // 3 = Laboratorio
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

    fun initializeWithUser(usuario: UsuarioDTO) {
        Log.d("ViewModel", "Inicializando con usuario: ${usuario.nombres}")
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == usuario.usuarioId }) {
            currentMembers.add(usuario)
            _members.value = currentMembers
        }
    }

    fun setSelectedHours(hours: String) {
        _selectedHours.value = hours
    }

    fun setFechaSeleccionada(fecha: String) {
        _fechaSeleccionada.value = fecha
    }

    fun getLaboratorioNombreById(id: Int) {
        viewModelScope.launch {
            try {
                val laboratorio = laboratorioApi.getById(id)
                _laboratorioNombre.value = laboratorio.nombre
            } catch (e: Exception) {
                _laboratorioNombre.value = "Desconocido"
            }
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

    fun addMember(member: UsuarioDTO) {
        val currentMembers = _members.value.toMutableList()
        if (currentMembers.none { it.usuarioId == member.usuarioId }) {
            currentMembers.add(member)
            _members.value = currentMembers
            Log.d(
                "ViewModel",
                "Miembro agregado: ${member.nombres}. Total miembros: ${_members.value.size}"
            )
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }
}