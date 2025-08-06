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
import edu.ucne.ureserve.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                // Obtener el proyector actual
                val proyector = _proyectores.value.find { it.proyectorId == id }
                    ?: throw IllegalArgumentException("Proyector con id $id no encontrado")

                // Mostrar estado de carga
                _isLoading.value = true

                // Llamar al API (usa tu endpoint actual que retorna ProyectoresDto directamente)
                val updatedProyector = api.update(
                    id = id,
                    proyector = ProyectoresDto(
                        proyectorId = id,
                        nombre = proyector.nombre,
                        cantidad = proyector.cantidad,
                        conectividad = proyector.conectividad,
                        disponible = disponible
                    )
                )

                // Actualizar localmente (la API respondió exitosamente)
                _proyectores.value = _proyectores.value.map { item ->
                    if (item.proyectorId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: "Error desconocido"}"
                // Refrescar datos desde el servidor
                cargarProyectores() // Asegúrate que este método exista en tu ViewModel
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadCubiculos(id: Int, disponible: Boolean) {
        viewModelScope.launch {
            try {
                // Obtener el proyector actual
                val cubiculo = _cubiculos.value.find { it.cubiculoId == id }
                    ?: throw IllegalArgumentException("Cubiculo con id $id no encontrado")

                // Mostrar estado de carga
                _isLoading.value = true

                val updatedCubiculo = apicubiculos.update(
                    id = id,
                    cubiculo = CubiculosDto(
                        cubiculoId = id,
                        nombre = cubiculo.nombre,
                        disponible = disponible
                    )
                )

                // Actualizar localmente (la API respondió exitosamente)
                _cubiculos.value = _cubiculos.value.map { item ->
                    if (item.cubiculoId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: "Error desconocido"}"
                // Refrescar datos desde el servidor
                cargarCubiculos()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadLaboratorio(id: Int, disponible: Boolean) {
        viewModelScope.launch {
            try {
                // Obtener los laboratirios actuales
                val proyector = _laboratorios.value.find { it.laboratorioId == id }
                    ?: throw IllegalArgumentException("Laboratorio con id $id no encontrado")

                // Mostrar estado de carga
                _isLoading.value = true

                // Llamar al API (usa tu endpoint actual que retorna ProyectoresDto directamente)
                val updatedLaboratorio = apilaboratorios.update(
                    id = id,
                    laboratorio = LaboratoriosDto(
                        laboratorioId = id,
                        nombre = proyector.nombre,
                        disponible = disponible
                    )
                )

                // Actualizar localmente (la API respondió exitosamente)
                _laboratorios.value = _laboratorios.value.map { item ->
                    if (item.laboratorioId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: "Error desconocido"}"
                // Refrescar datos desde el servidor
                cargarLaboratorios() // Asegúrate que este método exista en tu ViewModel
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarDisponibilidadRestaurantes(id: Int?, disponible: Boolean) {
        viewModelScope.launch {
            try {
                val restauranteId = id ?: throw IllegalArgumentException("ID no puede ser nulo")

                // Obtener los laboratirios actuales
                val restaurante = _restaurantes.value.find { it.restauranteId == id }
                    ?: throw IllegalArgumentException("Laboratorio con id $id no encontrado")

                // Mostrar estado de carga
                _isLoading.value = true

                // Llamar al API (usa tu endpoint actual que retorna ProyectoresDto directamente)
                val updatedRestaurante = apiRestaurantes.update(
                    id = id,
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

                // Actualizar localmente (la API respondió exitosamente)
                _laboratorios.value = _laboratorios.value.map { item ->
                    if (item.laboratorioId == id) item.copy(disponible = disponible) else item
                }

            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message ?: "Error desconocido"}"
                // Refrescar datos desde el servidor
                cargarRestaurantes()
            } finally {
                _isLoading.value = false
            }
        }
    }
}