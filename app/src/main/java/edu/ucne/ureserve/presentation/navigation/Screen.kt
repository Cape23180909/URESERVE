package edu.ucne.ureserve.presentation.navigation

import kotlinx.serialization.Serializable

sealed  class Screen {
    @Serializable
     data object LoadStart : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object CalendarioProyector: Screen()

    @Serializable
    data object CalendarioCubiculo: Screen()

    @Serializable
    data object CalendarioLaboratorio: Screen()
}