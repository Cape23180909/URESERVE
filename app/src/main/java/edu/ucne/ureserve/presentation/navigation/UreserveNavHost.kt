package edu.ucne.ureserve.presentation.navigation

import AgregarEstudianteScreen
import DetalleReservaEnCursoProyectorScreen
import ProyectorSwitchScreen
import ReservasenCursoRestauranteScreen
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
import androidx.navigation.navArgument
import edu.ucne.ureserve.data.local.database.UReserveDb
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.admin.DashboardAdminScreen
import edu.ucne.ureserve.presentation.cubiculos.CubiculoReservationScreen
import edu.ucne.ureserve.presentation.cubiculos.DashboardCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ExitosaCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ModificarReservaCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoScreen
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoViewModel
import edu.ucne.ureserve.presentation.dashboard.DashboardScreen
import edu.ucne.ureserve.presentation.empleados.CubiculoSwitchScreen
import edu.ucne.ureserve.presentation.empleados.DashboardEmpleadoScreen
import edu.ucne.ureserve.presentation.empleados.DetalleReservaEnCursoCubiculoScreen
import edu.ucne.ureserve.presentation.empleados.DetalleReservaEnCursoLaboratorioScreen
import edu.ucne.ureserve.presentation.empleados.DetalleReservaEnCursoRestauranteScreen
import edu.ucne.ureserve.presentation.empleados.EmpleadoCubiculoScreen
import edu.ucne.ureserve.presentation.empleados.EmpleadoLaboratorioScreen
import edu.ucne.ureserve.presentation.empleados.EmpleadoRestauranteScreen
import edu.ucne.ureserve.presentation.empleados.EmpleadoproyectoScreen
import edu.ucne.ureserve.presentation.empleados.LaboratorioSwitchScreen
import edu.ucne.ureserve.presentation.empleados.ReservasenCursoCubiculoScreen
import edu.ucne.ureserve.presentation.empleados.ReservasenCursoLaboratorioScreen
import edu.ucne.ureserve.presentation.empleados.ReservasenCursoProyectorScreen
import edu.ucne.ureserve.presentation.empleados.RestauranteSwitchScreen
import edu.ucne.ureserve.presentation.laboratorios.AgregarEstudianteScreenLaboratorio
import edu.ucne.ureserve.presentation.laboratorios.DashboardLaboratorioListScreen
import edu.ucne.ureserve.presentation.laboratorios.ExistosaLaboratorioScreen
import edu.ucne.ureserve.presentation.laboratorios.LaboratorioReservationScreen
import edu.ucne.ureserve.presentation.laboratorios.ModificarReservaLaboratorioScreen
import edu.ucne.ureserve.presentation.laboratorios.PlanificadorLaboratorioScreen
import edu.ucne.ureserve.presentation.laboratorios.ReservaLaboratorioScreen
import edu.ucne.ureserve.presentation.laboratorios.ReservaLaboratorioViewModel
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.login.LoadStartScreen
import edu.ucne.ureserve.presentation.login.LoginScreen
import edu.ucne.ureserve.presentation.login.ProfileScreen
import edu.ucne.ureserve.presentation.proyectores.PrevisualizacionProyectorScreen
import edu.ucne.ureserve.presentation.proyectores.ProjectorReservationScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaExitosaScreen
import edu.ucne.ureserve.presentation.proyectores.ReservaProyectorScreen
import edu.ucne.ureserve.presentation.reservas.DetallesReservacionScreen
import edu.ucne.ureserve.presentation.reservas.ModificarReservaProyectorScreen
import edu.ucne.ureserve.presentation.reservas.ReservaListScreen
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import edu.ucne.ureserve.presentation.restaurantes.ModificarReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.PagoRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.PagoSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistroReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RegistroReservaScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaExitosaSalonScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteExitosaScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaRestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.ReservaSalaVipScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteReservationcalendarioScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteScreen
import edu.ucne.ureserve.presentation.restaurantes.RestauranteTransferenciaScreen
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel
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
import edu.ucne.ureserve.presentation.salones.ModificarReservaSalaVipScreen
import edu.ucne.ureserve.presentation.salones.ModificarReservaSalonScreen
import edu.ucne.ureserve.presentation.salones.PagoSalonScreen
import edu.ucne.ureserve.presentation.salones.RegistroReservaSalonScreen
import edu.ucne.ureserve.presentation.salones.ReservaSalonScreen
import edu.ucne.ureserve.presentation.salones.TarjetaCreditoSalonScreen
import edu.ucne.ureserve.presentation.welcome.WelcomeScreen
import edu.ucne.ureserve.presentation.youtube.CanalYoutubeScreen
import java.util.Calendar

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UreserveNavHost(navController: NavHostController,uReserveDb: UReserveDb) {
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

        composable("login") {
            LoginScreen(onLoginSuccess = { usuario ->
                when (usuario.correoInstitucional) {
                    "admin.ureserve@ucne.edu.do" -> {
                        navController.navigate("dashboard_admin") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    "proyectores.ureserve@ucne.edu.do",
                    "laboratorio.ureserve@ucne.edu.do",
                    "cubiculos.ureserve@ucne.edu.do",
                    "restaurante.ureserve@ucne.edu.do" -> {
                        navController.navigate("dashboard_empleado") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate("welcome") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            })
        }

        composable("dashboard_admin") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            DashboardAdminScreen(
                usuario = usuario,
                onLogout = {
                    AuthManager.logout()
                    navController.navigate("LoadStart") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onOpcionesEmpleadoProyector = {
                    navController.navigate("empleadoproyecto")
                },
                navController = navController
            )
        }

        composable("Welcome") {
            WelcomeScreen(
                onContinue = {
                    navController.navigate("Dashboard") {
                        popUpTo("Welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("CanalYoutube") {
            CanalYoutubeScreen()
        }



        composable("modificar_proyector/{reservaId}") { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getString("reservaId")?.toIntOrNull()
            ModificarReservaProyectorScreen(
                reservaId = reservaId,
                navController = navController
            )
        }

        // Dentro de tu NavHost composable
        composable(
            route = "modificar_cubiculo/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0
            ModificarReservaCubiculoScreen(
                reservaId = reservaId,
                navController = navController
            )
        }


        composable(
            route = "modificar_restaurante/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0
            ModificarReservaRestauranteScreen(
                reservaId = reservaId,
                navController = navController
            )
        }
// Ruta para modificar reserva en salón VIP
        composable(
            route = "modificar_sala_vip/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0
            ModificarReservaSalaVipScreen(
                reservaId = reservaId,
                navController = navController
            )
        }


        composable(
            route = "modificar_salon/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0
            ModificarReservaSalonScreen(
                reservaId = reservaId,
                navController = navController
            )
        }




        composable("reservaList") {
            ReservaListScreen(
                navController = navController,
                onBottomNavClick = { route -> navController.navigate(route) }
            )
        }


        composable(
            route = "modificar_laboratorio/{reservaId}",
            arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0
            ModificarReservaLaboratorioScreen(
                reservaId = reservaId,
                navController = navController
            )
        }


        composable("Dashboard") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            // 2. Obtener el estudiante (igual que en Profile)
            val estudiante = remember {
                EstudianteDto(
                    estudianteId = 1,  // Esto debería venir de tu backend
                    matricula = "2022-0465",
                    facultad = "Ingeniería",
                    carrera = "Ingeniería en Sistemas"
                )
            }
            DashboardScreen(
                onCategoryClick = { category ->
                    when (category) {
                        "Proyectores" -> navController.navigate("ProjectorReservation")
                        "Cubículos" -> navController.navigate("CubiculoReservation")
                        "Laboratorios" -> navController.navigate("LaboratorioReservation")
                        "Restaurante" -> navController.navigate("RestauranteReservation")
                        "Reservaciones" -> navController.navigate("ReservaList")
                        {

                        }
                    }
                },
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Perfil" -> navController.navigate("Profile")
                        "Inicio" -> navController.navigate("Dashboard")
                        "Tutorial" -> navController.navigate("CanalYoutube")
                    }
                }
                ,
                onProfileIconClick = {
                    navController.navigate("Profile") // Navegación al hacer clic en el icono
                },
                usuario = usuario,
                estudiante = estudiante,
            )
        }

        composable("ReservaList") {
            // Verificación de autenticación y obtención de matrícula
            val usuario = AuthManager.currentUser
            if (usuario == null) {
                navController.navigate("Login") {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val matricula = usuario.estudiante?.matricula
            if (matricula.isNullOrBlank()) {
                // Manejar caso de matrícula no disponible
                return@composable
            }

            val viewModel: ReservaViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                viewModel.loadReservas()
            }

            ReservaListScreen(
                navController = navController,
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard") {
                            popUpTo("Dashboard") { inclusive = false }
                        }
                    }
                },
                viewModel = viewModel
            )
        }

        // En tu NavHost
        composable(
            "detallesReserva/{reservaId}/{fecha}/{horaInicio}/{horaFin}/{matricula}/{tipoReserva}",
            arguments = listOf(
                navArgument("reservaId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("matricula") { type = NavType.StringType },
                navArgument("tipoReserva") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            DetallesReservacionScreen(
                reservaId = backStackEntry.arguments?.getInt("reservaId") ?: 0,
                fecha = backStackEntry.arguments?.getString("fecha") ?: "",
                horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: "",
                horaFin = backStackEntry.arguments?.getString("horaFin") ?: "",
                matricula = backStackEntry.arguments?.getString("matricula") ?: "",
                tipoReserva = backStackEntry.arguments?.getString("tipoReserva") ?: "",
                navController = navController
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
                navController = navController,
                onBottomNavClick = { destination ->
                    when (destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                    }
                }
            )
        }

        val onBottomNavClick: (String) -> Unit = { screen ->
            when (screen) {
                "Inicio" -> navController.navigate("Dashboard")
            }
        }

        composable(
            route = "LaboratorioList/{fechaMillis}",
            arguments = listOf(navArgument("fechaMillis") { type = NavType.LongType })
        ) { backStackEntry ->
            val fechaMillis = backStackEntry.arguments?.getLong("fechaMillis")
            val calendar = Calendar.getInstance().apply {
                timeInMillis = fechaMillis ?: Calendar.getInstance().timeInMillis
            }

            // Inyectar el ViewModel correcto:
            val viewModel: ReservaLaboratorioViewModel = hiltViewModel()

            DashboardLaboratorioListScreen(
                selectedDateMillis = calendar.timeInMillis,
                onLaboratorioSelected = { laboratorioId, laboratorioNombre ->
                    navController.navigate("planificador_laboratorio/$laboratorioId/$laboratorioNombre/${calendar.timeInMillis}")
                },
                onBackClick = { navController.popBackStack() },
                navController = navController,
                onBottomNavClick = onBottomNavClick
            )
        }

        composable(
            "planificador_laboratorio/{laboratorioId}/{laboratorioNombre}/{fechaMillis}",
            arguments = listOf(
                navArgument("laboratorioId") { type = NavType.IntType },
                navArgument("laboratorioNombre") { type = NavType.StringType },
                navArgument("fechaMillis") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val laboratorioId = backStackEntry.arguments?.getInt("laboratorioId")
            val laboratorioNombre = backStackEntry.arguments?.getString("laboratorioNombre") ?: "No definido"
            val fechaMillis = backStackEntry.arguments?.getLong("fechaMillis") ?: 0L

            PlanificadorLaboratorioScreen(
                navController = navController,
                laboratorioId = laboratorioId,
                laboratorioNombre = laboratorioNombre,
                fechaSeleccionadaMillis = fechaMillis
            )
        }

        composable(
            route = "reservaLaboratorio/{laboratorioId}/{horaInicio}/{horaFin}/{fecha}",
            arguments = listOf(
                navArgument("laboratorioId") { type = NavType.IntType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val laboratorioId = backStackEntry.arguments?.getInt("laboratorioId")
            val horaInicio = backStackEntry.arguments?.getString("horaInicio").orEmpty()
            val horaFin = backStackEntry.arguments?.getString("horaFin").orEmpty()
            val fecha = backStackEntry.arguments?.getLong("fecha") ?: 0L
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
                horaFin = horaFin,
                fecha = fecha
            )
        }

        composable("AgregarEstudianteLaboratorio") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("reservaLaboratorio/{laboratorioId}/{horaInicio}/{horaFin}/{fecha}")
            }

            val viewModel: ReservaLaboratorioViewModel = hiltViewModel(parentEntry)

            AgregarEstudianteScreenLaboratorio(
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
            route = "ReservaLaboratorioExitosa/{codigo}",
            arguments = listOf(
                navArgument("codigo") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigo")
            ExistosaLaboratorioScreen(
                navController = navController,
                codigoReserva = codigoReserva
            )
        }


        composable("SalaVipReservation") {
            SalaVipReservationScreen(
                onBottomNavClick = { destination ->
                    when(destination) {
                        "Inicio" -> navController.navigate("Dashboard")
                        "Perfil" -> navController.navigate("Profile")
                        "Tutorial" -> navController.navigate("CanalYoutube")
                    }
                },
                navController = navController //  Esto es lo que faltaba
            )
        }

        composable(
            "ReservaProyector/{fecha}",
            arguments = listOf(navArgument("fecha") { type = NavType.StringType })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha")
            ReservaProyectorScreen(
                navController = navController,
                fecha = fecha
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
                    }
                },
                navController = navController
            )
        }
        composable("agregar_estudiante") {
            AgregarEstudianteScreen(
                navController = navController
            )
        }


        composable("agregar_estudiante_laboratorio") {
            AgregarEstudianteScreenLaboratorio(
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
                        "Tutorial" -> navController.navigate("CanalYoutube")
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
            route = "RegistroReservaSalon?fecha={fecha}",
            arguments = listOf(
                navArgument("fecha") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            RegistroReservaSalonScreen(
                fecha = fecha,
                onCancelarClick = {
                    navController.popBackStack()
                },
                onConfirmarClick = {
                    navController.navigate("pagoSalon?fecha=$fecha")
                }
            )
        }


        composable(
            route = "ReservaSalaVipExitosa/{numeroReserva}",
            arguments = listOf(navArgument("numeroReserva") { type = NavType.StringType })
        ) { backStackEntry ->
            val numeroReserva = backStackEntry.arguments?.getString("numeroReserva") ?: ""
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

        composable("PagoSalaVipScreen?fecha={fecha}") { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            PagoSalaVipScreen(fecha = fecha, navController = navController)
        }

        composable("RegistroReservaRestaurante?fecha={fecha}") { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val viewModel: RestaurantesViewModel = hiltViewModel()

            RegistroReservaRestauranteScreen(
                fecha = fecha,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = "ReservaSalon?fecha={fecha}",
            arguments = listOf(navArgument("fecha") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            ReservaSalonScreen(fecha = fecha, navController = navController)
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
                navController = navController,
                onCancelarClick = { navController.popBackStack() }
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

        //Empleado Proyector

        composable("dashboard_empleado") {
            val usuario = AuthManager.currentUser ?: UsuarioDTO()
            DashboardEmpleadoScreen(
                usuario = usuario,
                onLogout = {
                    AuthManager.logout()
                    navController.navigate("LoadStart") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onOpcionesEmpleadoProyector = {
                    navController.navigate("empleadoproyecto")
                },
                navController = navController
            )
        }

        composable("empleadoproyecto") {
            EmpleadoproyectoScreen(
                navController = navController // Asegúrate de recibirlo en el composable
            )
        }

        composable("empleadolaboratorio") {
            EmpleadoLaboratorioScreen(navController)
        }

        composable("empleadoCubiculo") {
            EmpleadoCubiculoScreen(navController)
        }

        composable("empleadoRestaurante") {
            EmpleadoRestauranteScreen(navController)
        }

        composable("empleadoproyector_En_Curso") {
            ReservasenCursoProyectorScreen(
                navController = navController // Asegúrate de recibirlo en el composable
            )
        }

        composable(
            route = "detalleReservaProyector/{codigoReserva}/{fecha}/{horaInicio}/{horaFin}/{matricula}",
            arguments = listOf(
                navArgument("codigoReserva") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("matricula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: ""
            val horaFin = backStackEntry.arguments?.getString("horaFin") ?: ""
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""

            val reserva = ReservacionesDto(
                codigoReserva = codigoReserva,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                matricula = matricula,
                tipoReserva = 1
            )

            DetalleReservaEnCursoProyectorScreen(
                navController = navController,
                reserva = reserva
            )
        }


        composable("empleadolaboratorio_En_Curso") {
            ReservasenCursoLaboratorioScreen(
                navController = navController // Asegúrate de recibirlo en el composable
            )
        }

        composable(
            route = "detalleReservaLaboratorio/{codigoReserva}/{fecha}/{horaInicio}/{horaFin}/{matricula}",
            arguments = listOf(
                navArgument("codigoReserva") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("matricula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: ""
            val horaFin = backStackEntry.arguments?.getString("horaFin") ?: ""
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""

            val reserva = ReservacionesDto(
                codigoReserva = codigoReserva,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                matricula = matricula,
                tipoReserva = 3
            )

            DetalleReservaEnCursoLaboratorioScreen(
                navController = navController,
                reserva = reserva
            )
        }

        composable("empleadocubiculo_En_Curso") {
            ReservasenCursoCubiculoScreen(
                navController = navController // Asegúrate de recibirlo en el composable
            )
        }

        composable(
            route = "detalleReservaCubiculo/{codigoReserva}/{fecha}/{horaInicio}/{horaFin}/{matricula}",
            arguments = listOf(
                navArgument("codigoReserva") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("matricula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: ""
            val horaFin = backStackEntry.arguments?.getString("horaFin") ?: ""
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""

            val reserva = ReservacionesDto(
                codigoReserva = codigoReserva,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                matricula = matricula,
                tipoReserva = 2
            )

            DetalleReservaEnCursoCubiculoScreen(
                navController = navController,
                reserva = reserva
            )
        }

        composable("empleadorestaurante_En_Curso") {
            ReservasenCursoRestauranteScreen(
                navController = navController // Asegúrate de recibirlo en el composable
            )
        }

        composable(
            route = "detalleReservaRestaurante/{codigoReserva}/{fecha}/{horaInicio}/{horaFin}/{matricula}/{tipoReserva}",
            arguments = listOf(
                navArgument("codigoReserva") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("horaInicio") { type = NavType.StringType },
                navArgument("horaFin") { type = NavType.StringType },
                navArgument("matricula") { type = NavType.StringType },
                navArgument("tipoReserva") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val codigoReserva = backStackEntry.arguments?.getInt("codigoReserva") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val horaInicio = backStackEntry.arguments?.getString("horaInicio") ?: ""
            val horaFin = backStackEntry.arguments?.getString("horaFin") ?: ""
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val tipoReserva = backStackEntry.arguments?.getInt("tipoReserva") ?: -1
            val reserva = ReservacionesDto(
                codigoReserva = codigoReserva,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                matricula = matricula,
                tipoReserva = tipoReserva
            )
            DetalleReservaEnCursoRestauranteScreen(
                navController = navController,
                reserva = reserva
            )
        }

        composable("proyector_switch") {
            ProyectorSwitchScreen(
                navController = navController
            )
        }

        composable("laboratorio_switch") {
            LaboratorioSwitchScreen(
                navController = navController
            )
        }

        composable("cubiculo_switch") {
            CubiculoSwitchScreen(
                navController = navController
            )
        }

        composable("restaurante_switch") {
            RestauranteSwitchScreen(
                navController = navController
            )
        }
    }
}