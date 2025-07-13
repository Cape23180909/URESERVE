package edu.ucne.ureserve.presentation.laboratorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.LaboratoriosApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
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
    private val reservaApi: ReservacionesApi,
    private val laboratorioApi: LaboratoriosApi
    ): ViewModel(){
    private val _members = MutableStateFlow<List<UsuarioDTO>>(emptyList())
    val members: StateFlow<List<UsuarioDTO>> = _members.asStateFlow()

    private val _selectedHours = MutableStateFlow("")
    val selectedHours: StateFlow<String> = _selectedHours.asStateFlow()

    private val _laboratorioNombre = MutableStateFlow("")
    val laboratorioNombre: StateFlow<String> = _laboratorioNombre

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReservaLaboratorio(
        cubiculoId: Int,
        cantidadHoras: Int,
        matricula: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val codigoReserva = (100000..999999).random()

                val fecha = ZonedDateTime.now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_INSTANT)

                val horario = String.format("%02d:00:00", cantidadHoras)

                val reservacionDto = ReservacionesDto(
                    codigoReserva = codigoReserva,
                    tipoReserva = 3, // 3 = Laboratorio
                    cantidadEstudiantes = members.value.size,
                    fecha = fecha,
                    horario = horario,
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
}