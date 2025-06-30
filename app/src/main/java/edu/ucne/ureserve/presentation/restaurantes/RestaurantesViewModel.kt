package edu.ucne.ureserve.presentation.restaurantes


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import edu.ucne.ureserve.data.repository.ReservacionRepository
import edu.ucne.ureserve.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantesViewModel @Inject constructor(
    private val restauranteRepository: RestauranteRepository,
    private val reservacionRepository : ReservacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getRestaurantes()
    }

    fun validarCampos(): Boolean {
        val state = _uiState.value
        return when {
            state.nombre.isBlank() -> {
                _uiState.update { it.copy(inputError = "El nombre no puede estar vacío") }
                false
            }
            state.ubicacion.isBlank() -> {
                _uiState.update { it.copy(inputError = "La ubicación no puede estar vacía") }
                false
            }
            state.capacidad <= 0 -> {
                _uiState.update { it.copy(inputError = "La capacidad debe ser mayor que cero") }
                false
            }
            state.telefono.isBlank() -> {
                _uiState.update { it.copy(inputError = "El teléfono no puede estar vacío") }
                false
            }
            state.correo.isBlank() -> {
                _uiState.update { it.copy(inputError = "El correo no puede estar vacío") }
                false
            }
            state.fecha.isBlank() -> {
                _uiState.update { it.copy(inputError = "La fecha no puede estar vacía") }
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
        val nuevoRestaurante = RestaurantesDto(
            restauranteId = 0,
            nombre = current.nombre,
            ubicacion = current.ubicacion,
            capacidad = current.capacidad,
            telefono = current.telefono,
            correo = current.correo,
            descripcion = current.descripcion
        )

        viewModelScope.launch {
            try {
                restauranteRepository.createRestaurante(nuevoRestaurante)
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
        val restauranteId = current.restauranteId ?: run {
            _uiState.update { it.copy(errorMessage = "ID nulo, no se puede actualizar") }
            return
        }

        val restauranteActualizado = RestaurantesDto(
            restauranteId = restauranteId,
            nombre = current.nombre,
            ubicacion = current.ubicacion,
            capacidad = current.capacidad,
            telefono = current.telefono,
            correo = current.correo,
            descripcion = current.descripcion
        )

        viewModelScope.launch {
            try {
                restauranteRepository.updateRestaurante(restauranteId, restauranteActualizado)
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
            _uiState.update { it.copy(isLoading = true) }
            try {
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
                        errorMessage = e.localizedMessage ?: "Error al cargar los restaurantes",
                        isLoading = false
                    )
                }
            }
        }
    }
    fun create(restaurante: DatosPersonalesRestaurante) {
        val nuevoRestaurante = RestaurantesDto(
            restauranteId = restaurante.restauranteId,
            nombre = restaurante.nombre,
            ubicacion = restaurante.ubicacion,
            capacidad = restaurante.capacidad,
            telefono = restaurante.telefono,
            correo = restaurante.correo,
            descripcion = restaurante.descripcion
        )

        viewModelScope.launch {
            try {
                restauranteRepository.createRestaurante(nuevoRestaurante)
                limpiarCampos()
                getRestaurantes()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Error al guardar") }
            }
        }
    }
    fun crearReservacionDesdeRestaurante(fecha: String, matricula: String) {
        val codigoReserva = (100000..999999).random()
        val fechaConHora = "${fecha}T12:00:00" // Puedes ajustar la hora

        val nuevaReservacion = ReservacionesDto(
            codigoReserva = codigoReserva,
            tipoReserva = 1, // Restaurante
            cantidadEstudiantes = 0,
            fecha = fechaConHora,
            horario = "01:00:00",
            estado = 1,
            matricula = matricula
        )

        viewModelScope.launch {
            try {
                reservacionRepository.createReservacion(nuevaReservacion)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al crear reservación: ${e.localizedMessage}")
                }
            }
        }
    }



    // Setters
    fun setNombre(value: String) = _uiState.update { it.copy(nombre = value) }
    fun setUbicacion(value: String) = _uiState.update { it.copy(ubicacion = value) }
    fun setCapacidad(value: Int) = _uiState.update { it.copy(capacidad = value) }
    fun setTelefono(value: String) = _uiState.update { it.copy(telefono = value) }
    fun setCorreo(value: String) = _uiState.update { it.copy(correo = value) }
    fun setDescripcion(value: String) = _uiState.update { it.copy(descripcion = value) }
    fun setRestauranteId(value: Int) = _uiState.update { it.copy(restauranteId = value) }
    fun setFecha(value: String) = _uiState.update { it.copy(fecha = value) }

    fun limpiarCampos() {
        _uiState.update {
            it.copy(
                restauranteId = null,
                nombre = "",
                ubicacion = "",
                capacidad = 0,
                telefono = "",
                correo = "",
                descripcion = "",
                fecha = "",
                inputError = null
            )
        }
    }
}

data class RestaurantesUiState(
    val restauranteId: Int? = null,
    val nombre: String = "",
    val ubicacion: String = "",
    val capacidad: Int = 0,
    val telefono: String = "",
    val correo: String = "",
    val descripcion: String = "",
    val fecha: String = "", // <--- AQUI INCLUIDA LA FECHA
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val restaurantes: List<RestaurantesDto> = emptyList()
)

