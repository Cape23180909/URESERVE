package edu.ucne.ureserve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.login.LoadStartScreen
import edu.ucne.ureserve.presentation.login.LoginScreen
import edu.ucne.ureserve.presentation.login.ProfileScreen

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
            val currentRoute = navController.currentBackStackEntry?.destination?.route

            DashboardScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Perfil" -> navController.navigate("Profile")
                        "Inicio" -> {
                            // Ya estamos en Dashboard, puedes recargar si es necesario
                            navController.navigate("Dashboard") {
                                popUpTo("Dashboard") { inclusive = false }
                            }
                        }
                        "Tutorial" -> navController.navigate("Tutorial")
                    }
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