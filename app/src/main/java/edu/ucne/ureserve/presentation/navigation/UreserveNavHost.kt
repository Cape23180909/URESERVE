package edu.ucne.ureserve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
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
                onLoginSuccess = {
                    navController.navigate("Dashboard") {
                        popUpTo("Login") { inclusive = true }
                    }
                },
                apiUrl = ""
            )
        }
        composable("Dashboard") {
            DashboardScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Perfil" -> navController.navigate("Profile")
                        "Inicio" -> navController.navigate("Dashboard")
                        "Tutorial" -> navController.navigate("Tutorial")
                    }
                }
            )
        }
        composable("Profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate("LoadStart") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Perfil" -> navController.navigate("Profile")
                        "Inicio" -> navController.navigate("Dashboard")
                        "Tutorial" -> navController.navigate("Tutorial")
                    }
                }
            )
        }
        // Agrega aquí la pantalla de Tutorial si la tienes
    }
}