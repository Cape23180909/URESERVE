package edu.ucne.ureserve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.cubiculos.CubiculoReservationScreen
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
import edu.ucne.ureserve.presentation.laboratorios.LaboratorioReservationScreen
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.login.LoadStartScreen
import edu.ucne.ureserve.presentation.login.LoginScreen
import edu.ucne.ureserve.presentation.login.ProfileScreen
import edu.ucne.ureserve.presentation.proyectores.ProjectorReservationScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaProyectorScreen
import edu.ucne.ureserve.presentation.restaurantes.ConfirmacionReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.ConfirmacionReservaReunionesScreen
import edu.ucne.ureserve.presentation.restaurantes.ConfirmacionReservaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.SalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.SalonReunionesScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaVipScreen

@Composable
fun UreserveNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "LoadStart"
    ) {
        composable("LoadStart") {
            LoadStartScreen(
                onTimeout = {
                    navController.navigate("Login") {
                        popUpTo("LoadStart") { inclusive = true }
                    }
                }
            )
        }

        composable("Login") {
            LoginScreen(
                onLoginSuccess = { usuario ->
                    AuthManager.login(usuario)
                    navController.navigate("Dashboard") {
                        popUpTo("Login") { inclusive = true }
                    }
                },
                apiUrl = ""
            )
        }

        composable("Dashboard") {
            DashboardScreen(
                onCategoryClick = { category ->
                    when (category) {
                        "Proyectores" -> navController.navigate("ProjectorReservation")
                        "Cubículos" -> navController.navigate("CubiculoReservation")
                        "Laboratorios" -> navController.navigate("LaboratorioReservation")
                        "Restaurante" -> navController.navigate("RestauranteReservation") // NUEVA

                        "Mis Reservas" -> {
                            // Agrega aquí si tienes una pantalla para "Mis Reservas"
                        }
                    }
                },
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Perfil" -> navController.navigate("Profile")
                        "Inicio" -> {} // Ya estás en Dashboard
                        "Tutorial" -> {} // Agrega si tienes esa pantalla
                    }
                }
            )
        }

        composable("ProjectorReservation") {
            ProjectorReservationScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                },
                navController = navController
            )
        }

        composable("CubiculoReservation") {
            CubiculoReservationScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                }
            )
        }

        composable("LaboratorioReservation") {
            LaboratorioReservationScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                }
            )
        }

        composable("ReservaProyector") {
            ReservaProyectorScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                },
                navController = navController
            )
        }

        composable("RestauranteReservation") {
            ReservaRestauranteScreen(
                onOptionClick = { option ->
                    when(option) {
                        "Sala VIP" -> navController.navigate("SalaVIP")
                        "Salón de reuniones" -> navController.navigate("SalonReuniones")
                        "Restaurante" -> navController.navigate("Restaurante")
                    }
                },
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> { /* ... */ }
                    }
                }
            )
        }

        composable("SalaVIP") { backStackEntry ->
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
            val terminosAceptados by backStackEntry
                .savedStateHandle
                .getStateFlow("vip_terminos_aceptados", false)
                .collectAsState()

            SalaVipScreen(
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("ConfirmacionReservaVIP")
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {}
                    }
                },
                onExclamacionClick = {
                    navController.navigate("TerminosReservaVip")
                }
            )
        }

        composable("ConfirmacionReservaVIP") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            ConfirmacionReservaVipScreen(
                nombreUsuario = usuario.nombres?: "Invitado",
                onVolverClick = {
                    navController.navigate("Dashboard") {
                        popUpTo("ConfirmacionReservaVIP") { inclusive = true }
                    }
                }
            )
        }

        composable("Restaurante") { backStackEntry ->
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
            val terminosAceptados by backStackEntry
                .savedStateHandle
                .getStateFlow("restaurantes_terminos_aceptados", false)
                .collectAsState()

            RestauranteScreen(
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("ConfirmacionReservaRestaurante")
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {} // Agrega aquí si tienes esa pantalla
                    }
                }
                ,
                onExclamacionClick = {
                    navController.navigate("TerminosReservaRestaurante")
                }
            )
        }

        composable("ConfirmacionReservaRestaurante") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            ConfirmacionReservaRestauranteScreen(
                nombreUsuario = usuario.nombres ?: "Invitado",
                onVolverClick = {
                    navController.navigate("Dashboard") {
                        popUpTo("ConfirmacionReservaRestaurante") { inclusive = true }
                    }
                }
            )
        }

        composable("SalonReuniones") { backStackEntry ->
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
            val terminosAceptados by backStackEntry
                .savedStateHandle
                .getStateFlow("reuniones_terminos_aceptados", false)
                .collectAsState()
            SalonReunionesScreen(
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("ConfirmacionReservaReuniones")
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {}
                    }
                } ,
                onExclamacionClick = {
                    navController.navigate("TerminosReserva")
                }
            )
        }

        composable("ConfirmacionReservaReuniones") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            ConfirmacionReservaReunionesScreen(
                nombreUsuario = usuario.nombres ?: "Invitado",
                onVolverClick = {
                    navController.navigate("Dashboard") {
                        popUpTo("ConfirmacionReservaReuniones") { inclusive = true }
                    }
                }
            )
        }

        composable("TerminosReserva") {
            TerminosReservaScreen(
                onAceptarClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("reuniones_terminos_aceptados", true)

                    navController.popBackStack()
                },
                onCancelarClick = {
                    navController.popBackStack() // Regresar
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("TerminosReservaVip") {
            TerminosReservaVipScreen(
                onAceptarClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("vip_terminos_aceptados", true)

                    navController.popBackStack()
                },
                onCancelarClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("TerminosReservaRestaurante") {
            TerminosReservaRestauranteScreen(
                onAceptarClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("restaurantes_terminos_aceptados", true)

                    navController.popBackStack()
                },
                onCancelarClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("Profile") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            // Necesitas obtener el estudiante asociado al usuario
            val estudiante = remember {
                EstudianteDto(
                    estudianteId = 1,  // Esto debería venir de tu backend
                    matricula = "2022-0465",
                    facultad = "Ingeniería",
                    carrera = "Ingeniería en Sistemas"
                )
            }
            val currentRoute = navController.currentBackStackEntry?.destination?.route

            ProfileScreen(
                usuario = usuario,
                estudiante = estudiante,  // Añade este parámetro
                onLogout = {
                    AuthManager.logout()
                    navController.navigate("LoadStart") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard") {
                            popUpTo("Profile") { inclusive = false }
                        }
                        "Perfil" -> {
                            // Ya estamos en Perfil
                        }
                        "Tutorial" -> navController.navigate("Tutorial")
                    }
                }
            )
        }
    }
}