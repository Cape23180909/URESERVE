package edu.ucne.ureserve.presentation.navigation

import AgregarEstudianteScreen
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.cubiculos.CubiculoReservationScreen
import edu.ucne.ureserve.presentation.cubiculos.DashboardCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ExitosaCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoViewModel
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
import edu.ucne.ureserve.presentation.laboratorios.DashboardLaboratorioListScreen
import edu.ucne.ureserve.presentation.laboratorios.LaboratorioReservationScreen
import edu.ucne.ureserve.presentation.laboratorios.PlanificadorLaboratorioScreen
import edu.ucne.ureserve.presentation.laboratorios.ReservaLaboratorioScreen
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.login.LoadStartScreen
import edu.ucne.ureserve.presentation.login.LoginScreen
import edu.ucne.ureserve.presentation.login.ProfileScreen
import edu.ucne.ureserve.presentation.proyectores.PrevisualizacionProyectorScreen
import edu.ucne.ureserve.presentation.proyectores.ProjectorReservationScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaExitosaScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaProyectorScreen
import edu.ucne.ureserve.presentation.restaurantes.DatosPersonalesRestaurante
import edu.ucne.ureserve.presentation.restaurantes.DatosPersonalesRestauranteStore
import edu.ucne.ureserve.presentation.restaurantes.PagoRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.PagoSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistroReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistroReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistrosReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaExitosaSalonScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteExitosaScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteReservationcalendarioScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteTransferenciaScreen
import edu.ucne.ureserve.presentation.restaurantes.SalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.SalaVipTransferenciaScreen
import edu.ucne.ureserve.presentation.restaurantes.SalonReunionesScreen
import edu.ucne.ureserve.presentation.restaurantes.SalonTransferenciaScreen
import edu.ucne.ureserve.presentation.restaurantes.TarjetaCreditoRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.TarjetaCreditoSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.TerminosReservaVipScreen
import edu.ucne.ureserve.presentation.salareuniones.SalonReunionesReservationScreen
import edu.ucne.ureserve.presentation.salavip.ReservaSalaVipExitosaScreen
import edu.ucne.ureserve.presentation.salavip.SalaVipReservationScreen
import edu.ucne.ureserve.presentation.salones.PagoSalonScreen
import edu.ucne.ureserve.presentation.salones.RegistroReservaSalonScreen
import edu.ucne.ureserve.presentation.salones.ReservaSalonScreen
import edu.ucne.ureserve.presentation.salones.TarjetaCreditoSalonScreen
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.util.Calendar

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
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
                },
                navController = navController
            )
        }

        composable(
            route = "ReservaCubiculo?fecha={fecha}",
            arguments = listOf(navArgument("fecha") { defaultValue = "Hoy" })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Hoy"
            DashboardCubiculoScreen(
                fecha = fecha,
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                },
                navController = navController,
                usuarioDTO = UsuarioDTO()
            )
        }



        composable(
            route = "reserva/{cubiculoId}?usuario={usuario}",
            arguments = listOf(
                navArgument("cubiculoId") { type = NavType.IntType },
                navArgument("usuario") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val cubiculoId = backStackEntry.arguments?.getInt("cubiculoId")
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            val estudiante = remember {
                EstudianteDto(
                    estudianteId = 1,
                    matricula = "2022-0465",
                    facultad = "Ingeniería",
                    carrera = "Ingeniería en Sistemas"
                )
            }

            // Guardamos el backStackEntry como referencia para otras pantallas
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("reserva/{cubiculoId}?usuario={usuario}")
            }

            // Este ViewModel se compartirá con otras rutas
            val viewModel: ReservaCubiculoViewModel = hiltViewModel(parentEntry)

            // Inicializar el usuario
            LaunchedEffect(usuario) {
                viewModel.initializeWithUser(usuario)
            }

            ReservaCubiculoScreen(
                cubiculoId = cubiculoId,
                navController = navController,
                usuarioDTO = usuario,
                estudiante = estudiante,
                viewModel = viewModel
            )
        }

        composable("AgregarEstudiante") { backStackEntry ->
            // Usamos el mismo ViewModel de la pantalla anterior
            val parentEntry = remember {
                navController.getBackStackEntry("reserva/{cubiculoId}?usuario={usuario}")
            }
            val viewModel: ReservaCubiculoViewModel = hiltViewModel(parentEntry)

            AgregarEstudianteScreen(
                viewModel = viewModel,
                navController = navController,
                onCancel = { navController.popBackStack() },
                onAdd = { matricula ->
                    viewModel.buscarUsuarioPorMatricula(matricula) { usuarioEncontrado ->
                        if (usuarioEncontrado != null) {
                            viewModel.addMember(usuarioEncontrado)
                            Log.d("AgregarEstudianteScreen", "Usuario agregado: ${usuarioEncontrado.nombres}")
                            navController.popBackStack()
                        } else {
                            viewModel.setError("Matrícula no válida")
                        }
                    }
                }
            )
        }

        composable(
            route = "ReservaCubiculoExitosa/{codigoReserva}",
            arguments = listOf(
                navArgument("codigoReserva") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva")
            ExitosaCubiculoScreen(
                navController = navController,
                codigoReserva = codigoReserva
            )
        }

        composable("LaboratorioReservation") {
            LaboratorioReservationScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                },
                onDateSelected = { selectedDate ->
                    // Navegar a la pantalla de lista de laboratorios con la fecha seleccionada
                    navController.navigate("LaboratorioList/${selectedDate.timeInMillis}")
                }
            )
        }

        composable(
            "LaboratorioList/{fechaMillis}",
            arguments = listOf(navArgument("fechaMillis") { type = NavType.LongType })
        ) { backStackEntry ->
            val fechaMillis = backStackEntry.arguments?.getLong("fechaMillis")
            val calendar = Calendar.getInstance().apply {
                timeInMillis = fechaMillis ?: timeInMillis
            }

            DashboardLaboratorioListScreen(
                selectedDate = calendar,
                onLaboratorioSelected = { laboratorioNombre ->
                    // Aquí podrías navegar a otra pantalla específica del laboratorio
                },
                onBackClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        composable(
            "planificador_laboratorio/{laboratorioId}/{laboratorioNombre}",
            arguments = listOf(
                navArgument("laboratorioId") { type = NavType.IntType },
                navArgument("laboratorioNombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val laboratorioId = backStackEntry.arguments?.getInt("laboratorioId")
            val laboratorioNombre = backStackEntry.arguments?.getString("laboratorioNombre")

            PlanificadorLaboratorioScreen(
                navController = navController,
                laboratorioId = laboratorioId,
                laboratorioNombre = laboratorioNombre ?: "No definido"
            )
        }


        composable(
            route = "reservaLaboratorio/{laboratorioId}/{horaInicio}/{horaFin}",
            arguments = listOf(
                navArgument("laboratorioId") { type = NavType.IntType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val laboratorioId = backStackEntry.arguments?.getInt("laboratorioId")
            val horaInicio = backStackEntry.arguments?.getString("horaInicio").orEmpty()
            val horaFin = backStackEntry.arguments?.getString("horaFin").orEmpty()
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            val estudiante = remember {
                EstudianteDto(
                    estudianteId = 1,
                    matricula = "2022-0465",
                    facultad = "Ingeniería",
                    carrera = "Ingeniería en Sistemas"
                )
            }

            ReservaLaboratorioScreen(
                laboratorioId = laboratorioId,
                navController = navController,
                usuarioDTO = usuario,
                estudiante = estudiante,
                horaInicio = horaInicio,
                horaFin = horaFin
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
                },
                onExclamacionClick = {
                    navController.navigate("TerminosReservaRestaurante")
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
        composable("RestauranteReservationcalendario") {
            RestauranteReservationcalendarioScreen(
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio"  -> navController.navigate("Dashboard")
                        "Perfil"  -> navController.navigate("Profile")
                        // …
                    }
                },
                navController = navController
            )
        }

        composable(
            route = "previsualizacion/{fecha}/{horaInicio}/{horaFin}/{proyectorJson}",
            arguments = listOf(
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("proyectorJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: "08:00 AM"
            val horaFin = backStackEntry.arguments?.getString("horaFin") ?: "09:00 AM"
            val proyectorJson = backStackEntry.arguments?.getString("proyectorJson") ?: ""

            PrevisualizacionProyectorScreen(
                navController = navController,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                proyectorJson = proyectorJson
            )
        }

        composable(
            route = "ReservaExitosa/{codigoReserva}",
            arguments = listOf(
                navArgument("codigoReserva") {
                    type = NavType.IntType // Asumimos que es un Int
                }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva")
            ReservaExitosaScreen(
                navController = navController,
                codigoReserva = codigoReserva
            )
        }

        composable(
            route = "RegistroReservaSalon?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                    nullable = false // nullable debe ser false si tienes defaultValue
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"

            RegistroReservaSalonScreen(
                fecha = fecha,
                onCancelarClick = {
                    navController.popBackStack()
                },
                onConfirmarClick = {
                    // Aquí puedes guardar o procesar los datos antes de navegar
                    navController.navigate("PagoSalon?fecha=$fecha")
                }

            )

        }

        composable(
            route = "PagoRestaurante?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"

            // Llama a tu pantalla pasando la fecha y el NavController
            PagoRestauranteScreen(
                fecha = fecha,
                navController = navController // Asegúrate de tener este navController disponible en el scope
            )
        }



// Pantalla de Pago de Salón de Reuniones
        composable(
            route = "PagoSalon?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = "Fecha no especificada"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            PagoSalonScreen(
                fecha = fecha,
                navController = navController
            )
        }


        composable(
            route = "RegistroReservaRestaurante?fecha={fecha}",
            arguments = listOf(navArgument("fecha") {
                type = NavType.StringType
                defaultValue = "Fecha no especificada"
            })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            RegistroReservaRestauranteScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = {
                    navController.navigate("PagoRestaurante?fecha=$fecha")
                }
            )
        }

        composable(
            route = "RegistroReservaSalaVip?fecha={fecha}",
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
                    // Navega a PagoSalaVip, pasando la fecha recibida
                    navController.navigate("PagoSalaVip?fecha=$fecha")
                }
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
            val fechaEncoded = backStackEntry.arguments?.getString("fecha") ?: "Fecha no especificada"
            val fecha = Uri.decode(fechaEncoded) // Decodificamos para evitar problemas con espacios
            PagoSalaVipScreen(fecha = fecha, navController = navController)
        }




        composable(
            route = "ReservaRestauranteExitosa?numeroReserva={numeroReserva}",
            arguments = listOf(
                navArgument("numeroReserva") {
                    type = NavType.StringType
                    defaultValue = "0000"
                }
            )
        ) { backStackEntry ->
            val numeroReserva = backStackEntry.arguments?.getString("numeroReserva") ?: "0000"
            ReservaRestauranteExitosaScreen(numeroReserva = numeroReserva, navController = navController)
        }

        composable(
            route = "ReservaExitosaSalon?numeroReserva={numeroReserva}",
            arguments = listOf(
                navArgument("numeroReserva") {
                    type = NavType.StringType
                    defaultValue = "0000"
                }
            )
        ) { backStackEntry ->
            val numeroReserva = backStackEntry.arguments?.getString("numeroReserva") ?: "0000"
            ReservaExitosaSalonScreen(numeroReserva = numeroReserva, navController = navController)
        }

        composable(
            route = "ReservaSalon?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            ReservaSalonScreen(fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = {
                    navController.navigate("ReservaSalon?fecha=$fecha")
                }
            )
        }

        composable(
            route = "ReservaSalaVipExitosa?numeroReserva={numeroReserva}",
            arguments = listOf(
                navArgument("numeroReserva") {
                    type = NavType.StringType
                    defaultValue = "0000"
                }
            )
        ) { backStackEntry ->
            val numeroReserva = backStackEntry.arguments?.getString("numeroReserva") ?: "0000"
            ReservaSalaVipExitosaScreen(numeroReserva = numeroReserva, navController = navController)
        }


        composable(
            route = "TarjetaCreditoSalaVip?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            TarjetaCreditoSalaVipScreen(
                fecha = fecha,
                navController = navController
            )
        }



        composable("ReservaSalaVip?fecha={fecha}") { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            ReservaSalaVipScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = {
                    navController.navigate("PagoSalaVip?fecha=$fecha")
                }
            )
        }


        composable("RegistroReservaRestaurante?fecha={fecha}") { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            RegistrosReservaRestauranteScreen(
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = { datos ->
                    DatosPersonalesRestauranteStore.lista.add(
                        DatosPersonalesRestaurante(
                            restauranteId = datos.restauranteId,
                            nombre = datos.nombre,
                            ubicacion = datos.ubicacion,
                            capacidad = datos.capacidad,
                            telefono = datos.telefono,
                            correo = datos.correo,
                            descripcion = datos.descripcion,
                            fecha = fecha // Pasamos la fecha desde el parámetro de la ruta
                        )
                    )
                    navController.navigate("PagoRestaurante?fecha=$fecha")
                }
            )
        }

        composable(
            route = "ReservaSalaVip?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            ReservaSalaVipScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onConfirmarClick = {
                    // Aquí puedes guardar datos en alguna store si lo necesitas
                    navController.navigate("PagoSalaVip?fecha=$fecha")

                }
            )
        }


        composable(
            "TarjetaCreditoRestaurante?fecha={fecha}",
            arguments = listOf(navArgument("fecha") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            TarjetaCreditoRestauranteScreen(fecha = fecha, navController = navController)
        }


        composable(
            route = "TarjetaCreditoSalon?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""   // Valor por defecto para que no sea nulo
                    nullable = false    // No acepta nulo, pero sí puede ser vacío por defecto
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            TarjetaCreditoSalonScreen(fecha = fecha, navController = navController)
        }


        composable(
            route = "SalaVipTransferencia?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            SalaVipTransferenciaScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onTransferenciaClick = { banco -> /* manejo banco si quieres */ },
                onConfirmarClick = { fechaConfirmacion ->
                    navController.navigate("RegistroReservaSalaVip?fecha=$fechaConfirmacion")
                }
            )
        }

        composable(
            route = "SalonTransferencia?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            SalonTransferenciaScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onTransferenciaClick = { banco ->
                    // Si deseas usar el banco seleccionado, puedes hacerlo aquí.
                },
                onConfirmarClick = { fechaConfirmada ->
                    // 🚀 Aquí navegas hacia RegistroReservaSalonScreen
                    navController.navigate("RegistroReservaSalon?fecha=$fechaConfirmada")
                }
            )
        }


        composable(
            route = "RestauranteTransferencia?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""

            RestauranteTransferenciaScreen(
                fecha = fecha,
                onCancelarClick = { navController.popBackStack() },
                onTransferenciaClick = { banco ->
                    // Si quieres guardar el banco seleccionado o mostrarlo, hazlo aquí
                },
                onConfirmarClick = { fechaConfirmada ->
                    navController.navigate("RegistroReservaRestaurante?fecha=$fechaConfirmada")
                }
            )
        }
    }
}