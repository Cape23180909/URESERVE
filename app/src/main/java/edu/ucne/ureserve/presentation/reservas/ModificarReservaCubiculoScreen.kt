package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.login.AuthManager
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarReservaCubiculoScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: ReservaCubiculoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCubiculoDropdown by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.NOON) }
    var endTime by remember { mutableStateOf(LocalTime.NOON.plusHours(1)) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
// Cargar reserva al iniciar
    LaunchedEffect(reservaId) {
        if (reservaId != null && reservaId > 0) {
            viewModel.cargarReserva(reservaId)
        } else {
            errorMessage = "ID de reserva inválido"
            showErrorDialog = true
        }
    }


    // Manejar errores del estado
    LaunchedEffect(state.error) {
        state.error?.let {
            errorMessage = it
            showErrorDialog = true
        }
    }

    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                val reserva = viewModel.reservaApi.getById(id)
                val detalles = viewModel.detalleReservaApi.getAll().filter { it.codigoReserva == id }

                // Validaciones antes de parsear
                if (!reserva.fecha.isNullOrBlank()) {
                    val fechaParseada = try {
                        LocalDate.parse(reserva.fecha.substring(0, 10))
                    } catch (e: Exception) {
                        errorMessage = "Error al parsear la fecha: ${e.message}"
                        null
                    }

                    fechaParseada?.let {
                        selectedDate = it
                    }
                }

                startTime = try {
                    LocalTime.parse(reserva.horaInicio)
                } catch (e: Exception) {
                    LocalTime.of(12, 0) // Fallback seguro
                }

                endTime = try {
                    LocalTime.parse(reserva.horaFin)
                } catch (e: Exception) {
                    startTime.plusHours(1)
                }

                if (detalles.isNotEmpty()) {
                    try {
                        val cubiculo = viewModel.cubiculoApi.getById(detalles.first().idCubiculo)
                        viewModel.seleccionarCubiculo(cubiculo)
                    } catch (e: Exception) {
                        errorMessage = "Error al cargar cubículo: ${e.message}"
                    }
                }

                viewModel.verificarDisponibilidad(
                    selectedDate.toString(),
                    startTime.toString(),
                    endTime.toString()
                )
            } catch (e: Exception) {
                errorMessage = "Error al cargar la reserva: ${e.message}"
            }
        }
    }


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_reserve),
                                contentDescription = "Logo",
                                modifier = Modifier.size(50.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Reserva",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6D87A4)
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF023E8A))
                )
            }
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "MODIFICAR RESERVA DE CUBÍCULO",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reserva actual:", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}", color = Color.White)
                    Text("Hora: $startTime - $endTime", color = Color.White)
                    state.cubiculoSeleccionado?.let {
                        Text("Cubículo: ${it.nombre}", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7))
            ) {
                Text("Cambiar Fecha", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48CAE4))
            ) {
                Text("Cambiar Horario", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController?.navigate("agregar_estudiante")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADE8F4))
            ) {
                Text("Modificar Integrantes", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    reservaId?.let { id ->
                        viewModel.modificarReservaCubiculo(
                            reservaId = id,
                            cubiculoId = state.cubiculoSeleccionado?.cubiculoId ?: 0,
                            fechaLocal = selectedDate,
                            horaInicio = startTime,
                            horaFin = endTime,
                            matricula = AuthManager.currentUser?.estudiante?.matricula ?: ""
                        )

                        navController?.navigate("reservaList") {
                            popUpTo("modificarReservaCubiculo") { inclusive = true }
                        }
                    }
                },
                enabled = state.cubiculoSeleccionado != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
            ) {
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
            ) {
                Text("REGRESAR", fontWeight = FontWeight.Bold)
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(millis),
                                ZoneId.systemDefault()
                            ).toLocalDate()
                            viewModel.verificarDisponibilidad(
                                selectedDate.toString(),
                                startTime.toString(),
                                endTime.toString()
                            )
                        }
                        showDatePicker = false
                    }) {
                        Text("CONFIRMAR")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("CANCELAR")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val startState = rememberTimePickerState(startTime.hour, startTime.minute)
            val endState = rememberTimePickerState(endTime.hour, endTime.minute)

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Seleccionar Horario") },
                text = {
                    Column {
                        Text("Hora de inicio")
                        TimePicker(state = startState)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Hora de fin")
                        TimePicker(state = endState)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        startTime = LocalTime.of(startState.hour, startState.minute)
                        endTime = LocalTime.of(endState.hour, endState.minute)
                        showTimePicker = false
                        viewModel.verificarDisponibilidad(
                            selectedDate.toString(),
                            startTime.toString(),
                            endTime.toString()
                        )
                    }) {
                        Text("CONFIRMAR")
                    }
                },
                dismissButton = {
                    Button(onClick = { showTimePicker = false }) {
                        Text("CANCELAR")
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewModificarReservaCubiculoScreen() {
    ModificarReservaCubiculoScreen(navController = null)
}