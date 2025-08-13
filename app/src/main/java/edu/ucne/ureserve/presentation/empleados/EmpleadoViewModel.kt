package edu.ucne.ureserve.presentation.empleados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.LaboratoriosApi
import edu.ucne.ureserve.data.remote.ProyectoresApi
import edu.ucne.ureserve.data.remote.RestaurantesApi
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ERROR_DESCONOCIDO = "Error desconocido"

@HiltViewModel
class EmpleadoViewModel @Inject constructor(
    private val api: ProyectoresApi,
    private val apilaboratorios: LaboratoriosApi,
    private val apicubiculos: CubiculosApi,
    private val apiRestaurantes: RestaurantesApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmpleadoUiState())
    val uiState: StateFlow<EmpleadoUiState> = _uiState

    private val _proyectores = MutableStateFlow<List<ProyectoresDto>>(emptyList())
    val proyectores: StateFlow<List<ProyectoresDto>> = _proyectores

    private val _laboratorios = MutableStateFlow<List<LaboratoriosDto>>(emptyList())
    val laboratorios: StateFlow<List<LaboratoriosDto>> = _laboratorios

    private val _cubiculos = MutableStateFlow<List<CubiculosDto>>(emptyList())
    val cubiculos: StateFlow<List<CubiculosDto>> = _cubiculos

    private val _restaurantes = MutableStateFlow<List<RestaurantesDto>>(emptyList())
    val restaurantes: StateFlow<List<RestaurantesDto>> = _restaurantes

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarProyectores()
        cargarCubiculos()
        cargarLaboratorios()
        cargarRestaurantes()
    }

    fun cargarProyectores() {
        viewModelScope.launch {
            try {
                _error.value = null
                _isLoading.value = true
                val lista = api.getAll()
                _proyectores.value = lista
            } catch (e: Exception) {
                _error.value = "Error al cargar proyectores: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarCubiculos() {
        viewModelScope.launch {
            try {
                _error.value = null
                _isLoading.value = true
                val lista = apicubiculos.getAll()
                _cubiculos.value = lista
            } catch (e: Exception) {
                _error.value = "Error al cargar cubiculos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarLaboratorios() {
        viewModelScope.launch {
            try {
                _error.value = null
                _isLoading.value = true
                val lista = apilaboratorios.getAll()
                _laboratorios.value = lista
            } catch (e: Exception) {
                _error.value = "Error al cargar laboratorios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarRestaurantes() {
        viewModelScope.launch {
            try {
                _error.value = null
                _isLoading.value = true
                val lista = apiRestaurantes.getAll()
                _restaurantes.value = lista
            } catch (e: Exception) {
                _error.value = "Error al cargar restaurantes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadProyectores(id: Int, disponible: Boolean) {
        viewModelScope.launch {
            try {
                val proyector = _proyectores.value.find { it.proyectorId == id }
                    ?: throw IllegalArgumentException("Proyector con id $id no encontrado")

                _isLoading.value = true

                api.update(
                    id = id,
                    proyector = ProyectoresDto(
                        proyectorId = id,
                        nombre = proyector.nombre,
                        cantidad = proyector.cantidad,
                        conectividad = proyector.conectividad,
                        disponible = disponible
                    )
                )

                _proyectores.value = _proyectores.value.map { item ->
                    if (item.proyectorId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: ERROR_DESCONOCIDO}"

                cargarProyectores()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadCubiculos(id: Int, disponible: Boolean) {
        viewModelScope.launch {
            try {
                val cubiculo = _cubiculos.value.find { it.cubiculoId == id }
                    ?: throw IllegalArgumentException("Cubiculo con id $id no encontrado")

                _isLoading.value = true

                apicubiculos.update(
                    id = id,
                    cubiculo = CubiculosDto(
                        cubiculoId = id,
                        nombre = cubiculo.nombre,
                        disponible = disponible
                    )
                )

                _cubiculos.value = _cubiculos.value.map { item ->
                    if (item.cubiculoId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: ERROR_DESCONOCIDO}"
                cargarCubiculos()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadLaboratorio(id: Int, disponible: Boolean) {
        viewModelScope.launch {
            try {
                val laboratorio = _laboratorios.value.find { it.laboratorioId == id }
                    ?: throw IllegalArgumentException("Laboratorio con id $id no encontrado")

                _isLoading.value = true

                apilaboratorios.update(
                    id = id,
                    laboratorio = LaboratoriosDto(
                        laboratorioId = id,
                        nombre = laboratorio.nombre,
                        disponible = disponible
                    )
                )

                _laboratorios.value = _laboratorios.value.map { item ->
                    if (item.laboratorioId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: ERROR_DESCONOCIDO}"
                cargarLaboratorios()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadRestaurantes(id: Int?, disponible: Boolean) {
        viewModelScope.launch {
            try {
                val restaurante = _restaurantes.value.find { it.restauranteId == id }
                    ?: throw IllegalArgumentException("Restaurante con id $id no encontrado")

                _isLoading.value = true

                apiRestaurantes.update(
                    id = id!!,
                    restaurante = RestaurantesDto(
                        restauranteId = id,
                        nombre = restaurante.nombre,
                        ubicacion = restaurante.ubicacion,
                        capacidad = restaurante.capacidad,
                        telefono = restaurante.telefono,
                        correo = restaurante.correo,
                        descripcion = restaurante.descripcion,
                        disponible = disponible
                    )
                )

                _restaurantes.value = _restaurantes.value.map { item ->
                    if (item.restauranteId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: ERROR_DESCONOCIDO}"
                cargarRestaurantes()
            } finally {
                _isLoading.value = false
            }
        }
    }
}