package edu.ucne.ureserve.presentation.cubiculos

import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import java.time.LocalDate
import java.time.LocalTime

data class ReservaCubiculoState(
    val reservaId: Int? = null,
    val codigoReserva: Int? = null,
    val cantidadEstudiantes: Int = 0,
    val fecha: LocalDate? = null,
    val horaInicio: LocalTime? = null,
    val horaFin: LocalTime? = null,
    val estado: Int? = null,
    val matricula: String? = null,
    val fechaString: String? = null,
    val horaInicioString: String? = null,
    val horaFinString: String? = null,
    val cubiculos: List<CubiculosDto> = emptyList(),
    val cubiculoSeleccionado: CubiculosDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val disponibilidadVerificada: Boolean = false,
    val reservaConfirmada: Boolean = false,
    val miembros: List<UsuarioDTO> = emptyList()
)
