package edu.ucne.ureserve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.cubiculos.CubiculoReservationScreen
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
import edu.ucne.ureserve.presentation.laboratorios.LaboratorioReservationScreen
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.login.LoadStartScreen
import edu.ucne.ureserve.presentation.login.LoginScreen
import edu.ucne.ureserve.presentation.login.ProfileScreen
import edu.ucne.ureserve.presentation.proyectores.PrevisualizacionProyectorScreen
import edu.ucne.ureserve.presentation.proyectores.ProjectorReservationScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaExitosaScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaProyectorScreen
import edu.ucne.ureserve.presentation.restaurantes.PagoSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistroReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteReservationcalendarioScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.SalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.SalonReunionesScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaVipScreen
import edu.ucne.ureserve.presentation.salareuniones.SalonReunionesReservationScreen
import edu.ucne.ureserve.presentation.salavip.SalaVipReservationScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaExitosaScreen
import edu.ucne.ureserve.presentation.restaurantes.TarjetaCreditoSalaVipScreen


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

        composable("SalaVipReservation") {
            SalaVipReservationScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {}
                    }
                },
                navController = navController // ✅ Esto es lo que faltaba
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


        composable(
            route = "TarjetaCreditoSalaVip?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            TarjetaCreditoSalaVipScreen(navController = navController)
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
                navController = navController, // Pasa el navController
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("SalaVipReservation")
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



        composable("Restaurante") { backStackEntry ->
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
            val terminosAceptados by backStackEntry
                .savedStateHandle
                .getStateFlow("restaurantes_terminos_aceptados", false)
                .collectAsState()

            RestauranteScreen(
                navController = navController, // Pasa el navController
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("RestauranteReservationcalendario")
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

        composable("RestauranteReservationcalendario") {
            RestauranteReservationcalendarioScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {}
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
                navController = navController, // Pasa el navController
                terminosAceptados = terminosAceptados,
                onCancelClick = {
                    navController.popBackStack()
                },
                onConfirmClick = {
                    if (terminosAceptados) {
                        navController.navigate("SalonReunionesReservation")
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
// Add this composable route to your NavHost
        composable("SalonReunionesReservation") {
            SalonReunionesReservationScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> {}
                    }
                },
                navController = navController
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

        composable(
            route = "previsualizacion/{fecha}/{horaInicio}/{horaFin}",
            arguments = listOf(
                navArgument("fecha") { type = NavType.StringType; nullable = true },
                navArgument("horaInicio") { type = NavType.StringType; nullable = false },
                navArgument("horaFin") { type = NavType.StringType; nullable = false }
            )
        ) {
            val fecha = it.arguments?.getString("fecha")
            val horaInicio = it.arguments?.getString("horaInicio")
            val horaFin = it.arguments?.getString("horaFin")

            PrevisualizacionProyectorScreen(
                onBack = { navController.navigate("ProjectorReservation") { popUpTo("ProjectorReservation") { inclusive = false } } },
                onFinish = { /* Acción finalizar reserva */ },
                fecha = fecha,
                horaInicio = horaInicio ?: "08:00 AM",
                horaFin = horaFin ?: "09:00 AM",
                navController = navController
            )
        }
        composable(
            route = "PagoSalaVip?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            PagoSalaVipScreen(
                fecha = fecha,
                navController = navController
            )
        }

        composable(
            route = "RegistroReserva?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            RegistroReservaScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = {
                    navController.navigate("PagoSalaVip?fecha=$fecha")
                }
            )
        }

        composable(
            route = "ReservaExitosa?numeroReserva={numeroReserva}",
            arguments = listOf(
                navArgument("numeroReserva") {
                    type = NavType.StringType
                    defaultValue = "0000"
                }
            )
        ) { backStackEntry ->
            val numeroReserva = backStackEntry.arguments?.getString("numeroReserva") ?: "0000"
            ReservaExitosaScreen(numeroReserva = numeroReserva, navController = navController)
        }

    }
}

