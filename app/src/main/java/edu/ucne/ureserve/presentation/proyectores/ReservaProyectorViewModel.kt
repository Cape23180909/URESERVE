package edu.ucne.ureserve.presentation.proyectores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.dto.CreateDetalleReservaProyectorDto
import edu.ucne.ureserve.data.repository.ProyectorRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservaProyectorViewModel @Inject constructor(
    private val repository: ProyectorRepository
) : ViewModel() {

    fun createReserva(
        reservaDto: CreateDetalleReservaProyectorDto,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createDetalleReservaProyector(reservaDto)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}