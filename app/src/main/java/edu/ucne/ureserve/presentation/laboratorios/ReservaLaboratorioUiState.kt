package edu.ucne.ureserve.presentation.laboratorios

import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import java.time.LocalDate
import java.time.LocalTime

data class ReservaLaboratorioUiState(
    val reservaId: Int? = null,
    val codigoReserva: Int? = null,
    val tipoReserva: Int = 3,
    val cantidadEstudiantes: Int = 0,
    val fecha: LocalDate? = null,
    val horaInicio: LocalTime? = null,
    val horaFin: LocalTime? = null,
    val estado: Int = 1,
    val matricula: String = "",
    val laboratorioId: Int? = null,
    val laboratorioNombre: String = "",
    val miembros: List<UsuarioDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val fechaString: String = "",
    val horaInicioString: String = "",
    val horaFinString: String = ""
)