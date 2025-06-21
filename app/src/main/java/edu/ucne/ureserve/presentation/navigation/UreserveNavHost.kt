package edu.ucne.ureserve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
                }
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


        composable("SalaVIP") {
            SalaVipScreen(
                onCancelClick = {
                    navController.popBackStack() // Volver atrás
                },
                onConfirmClick = {
                    // Acción al confirmar
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {} // Si tienes esta pantalla
                    }
                } ,
                onExclamacionClick = {
                    navController.navigate("TerminosReservaVip")
                }
            )
        }
        composable("Restaurante") {
            RestauranteScreen(
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    // Acción al confirmar
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


        composable("SalonReuniones") {
            SalonReunionesScreen(
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    // Acción al confirmar
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
        composable("TerminosReserva") {
            TerminosReservaScreen(
                onAceptarClick = {
                    // Acción al aceptar
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
                    // Acción al aceptar (puedes navegar a otro lado si quieres)
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
                    // Acción al aceptar
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
        // Agrega aquí la pantalla de Tutorial si existe
    }
}