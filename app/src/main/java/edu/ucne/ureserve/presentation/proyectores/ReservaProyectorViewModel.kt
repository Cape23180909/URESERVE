package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.repository.ProyectorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    private val repository: ProyectorRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ReservaProyectorState())
    val state: StateFlow<ReservaProyectorState> = _state.asStateFlow()

    // Verificar disponibilidad
    fun verificarDisponibilidad(fecha: String, horaInicio: String, horaFin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val proyectores = repository.getProyectoresDisponibles(fecha, horaInicio, horaFin)
                _state.value = _state.value.copy(
                    proyectores = proyectores,
                    isLoading = false,
                    disponibilidadVerificada = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al verificar disponibilidad: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // Crear reserva
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearReserva(
        fecha: String,
        horaInicio: String,
        horaFin: String,
        proyectorId: Int,
        usuarioId: String = "usuario_x" // Temporal, luego implementar autenticación
    ): Boolean {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fechaLocalDate = LocalDate.parse(fecha, formatter)
            val reservaDto = ProyectoresDto(
                fecha = fechaLocalDate.toString(),
                horario = horaInicio,
                estado = 1, // 1 = Reservado
                codigoReserva = generarCodigoReserva(),
                proyectorId = proyectorId
            )

            val response = repository.createDetalleReservaProyector(reservaDto)
            response.isSuccessful
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al crear reserva: ${e.message}")
            false
        }
    }

    private fun generarCodigoReserva(): Int {
        return (100000..999999).random()
    }

    fun limpiarError() {
        _state.value = _state.value.copy(error = null)
    }

    suspend fun confirmarReserva(dto: ProyectoresDto): Boolean {
        return try {
            val response = repository.createDetalleReservaProyector(dto)
            response.isSuccessful
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error al confirmar reserva: ${e.message}")
            false
        }
    }
}

data class ReservaProyectorState(
    val proyectores: List<ProyectoresDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaActual: DetalleReservaProyectorsDto? = null
)