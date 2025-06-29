package edu.ucne.ureserve.presentation.restaurantes


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import edu.ucne.ureserve.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantesViewModel @Inject constructor(
    private val restauranteRepository: RestauranteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getRestaurantes()
    }

    fun validarCampos(): Boolean {
        val estado = _uiState.value.estado
        val cantidad = _uiState.value.cantidadEstudiantes
        val fecha = _uiState.value.fecha
        val horario = _uiState.value.horario

        return when {
            fecha.isBlank() -> {
                _uiState.update { it.copy(inputError = "La fecha no puede estar vacía") }
                false
            }
            horario.isBlank() -> {
                _uiState.update { it.copy(inputError = "El horario no puede estar vacío") }
                false
            }
            cantidad <= 0 -> {
                _uiState.update { it.copy(inputError = "Cantidad debe ser mayor que cero") }
                false
            }
            estado !in 0..1 -> {
                _uiState.update { it.copy(inputError = "Estado inválido (debe ser 0 o 1)") }
                false
            }
            else -> {
                _uiState.update { it.copy(inputError = null) }
                true
            }
        }
    }

    fun create() {
        if (!validarCampos()) return

        val current = _uiState.value
        val newReserva = RestaurantesDto(
            fecha = current.fecha,
            horario = current.horario,
            cantidadEstudiantes = current.cantidadEstudiantes,
            estado = current.estado,
            codigoReserva = current.codigoReserva
        )

        viewModelScope.launch {
            try {
                restauranteRepository.createRestaurante(newReserva)
                limpiarCampos()
                getRestaurantes()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Error al guardar") }
            }
        }
    }

    fun update() {
        if (!validarCampos()) return

        val current = _uiState.value
        val restauranteId = current.restauranteId

        if (restauranteId == null) {
            _uiState.update { it.copy(errorMessage = "No se puede actualizar: ID nulo") }
            return
        }

        val updatedReserva = RestaurantesDto(
            restauranteId = restauranteId,
            fecha = current.fecha,
            horario = current.horario,
            cantidadEstudiantes = current.cantidadEstudiantes,
            estado = current.estado,
            codigoReserva = current.codigoReserva
        )

        viewModelScope.launch {
            try {
                restauranteRepository.updateRestaurante(restauranteId, updatedReserva)
                limpiarCampos()
                getRestaurantes()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Error al actualizar")
                }
            }
        }
    }


    fun getRestaurantes() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val data = restauranteRepository.getRestaurantes()
                _uiState.update {
                    it.copy(
                        restaurantes = data as List<RestaurantesDto>,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.localizedMessage ?: "Error al cargar datos",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Setters
    fun setFecha(value: String) = _uiState.update { it.copy(fecha = value) }
    fun setHorario(value: String) = _uiState.update { it.copy(horario = value) }
    fun setCantidad(value: Int) = _uiState.update { it.copy(cantidadEstudiantes = value) }
    fun setEstado(value: Int) = _uiState.update { it.copy(estado = value) }
    fun setCodigoReserva(value: Int) = _uiState.update { it.copy(codigoReserva = value) }
    fun setRestauranteId(value: Int) = _uiState.update { it.copy(restauranteId = value) }

    fun limpiarCampos() {
        _uiState.update {
            it.copy(
                restauranteId = null,
                fecha = "",
                horario = "",
                cantidadEstudiantes = 0,
                estado = 0,
                codigoReserva = 0,
                inputError = null
            )
        }
    }
}
data class RestaurantesUiState(
    val restauranteId: Int? = null,
    val fecha: String = "",
    val horario: String = "",
    val cantidadEstudiantes: Int = 0,
    val estado: Int = 0,
    val codigoReserva: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val restaurantes: List<RestaurantesDto> = emptyList(),


)