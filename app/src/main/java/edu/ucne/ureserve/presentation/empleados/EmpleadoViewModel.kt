package edu.ucne.ureserve.presentation.empleados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.ProyectoresApi
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpleadoViewModel @Inject constructor(
    private val api: ProyectoresApi
) : ViewModel() {

    private val _proyectores = MutableStateFlow<List<ProyectoresDto>>(emptyList())
    val proyectores: StateFlow<List<ProyectoresDto>> = _proyectores

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarProyectores()
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

    fun actualizarDisponibilidad(id: Int, disponible: Boolean) {
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
}