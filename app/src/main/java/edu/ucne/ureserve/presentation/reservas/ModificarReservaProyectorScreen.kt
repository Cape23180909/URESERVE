package edu.ucne.ureserve.presentation.reservas

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
import edu.ucne.ureserve.presentation.proyectores.ReservaProyectorViewModel
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarReservaProyectorScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: ReservaProyectorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showProyectorDropdown by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.NOON) }
    var endTime by remember { mutableStateOf(LocalTime.NOON.plusHours(1)) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                val reserva = viewModel.reservaApi.getById(id)
                val detalles = viewModel.getDetallesReservaProyector(id)

                selectedDate = LocalDate.parse(reserva.fecha.substring(0, 10))
                startTime = LocalTime.parse(reserva.horaInicio)
                endTime = LocalTime.parse(reserva.horaFin)

                if (detalles.isNotEmpty()) {
                    val proyector = viewModel.repository.getProyector(detalles.first().idProyector)
                    viewModel.seleccionarProyector(proyector)
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
                text = "MODIFICAR RESERVA DE PROYECTOR",
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
                    reservaId?.let { id ->
                        viewModel.modificarReservaProyector(
                            reservaId = id,
                            proyectorId = state.proyectorSeleccionado?.proyectorId ?: 0,
                            fechaLocal = selectedDate,
                            horaInicio = startTime,
                            horaFin = endTime,
                            matricula = AuthManager.currentUser?.estudiante?.matricula ?: ""
                        )

                        // Navegar a la pantalla de lista de reservas
                        navController?.navigate("reservaList") {
                            popUpTo("modificarReservaProyector") { inclusive = true }
                        }
                    }
                },
                enabled = true,
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


        // TimePicker
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
fun PreviewModificarReservaProyectorScreen() {
    ModificarReservaProyectorScreen(navController = null)
}