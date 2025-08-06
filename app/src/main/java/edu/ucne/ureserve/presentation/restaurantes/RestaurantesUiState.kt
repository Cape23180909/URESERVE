package edu.ucne.ureserve.presentation.restaurantes

import edu.ucne.ureserve.data.remote.dto.RestaurantesDto


data class RestaurantesUiState(
    val restauranteId: Int? = null,
    val correo: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val matricula: String = "",
    val cedula: String = "",
    val direccion: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val metodoPagoSeleccionado: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val reservaConfirmada: Boolean = false,
    val mensaje: String? = null,
    val restaurantes: List<RestaurantesDto> = emptyList(),
    val reservas: List<Map<String, Any?>> = emptyList()
)