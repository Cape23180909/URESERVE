package edu.ucne.ureserve.presentation.restaurantes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ModificarReservaRestauranteScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados para fecha y hora
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.NOON) }
    var endTime by remember { mutableStateOf(LocalTime.NOON.plusHours(1)) }

    // Estados originales para comparar cambios
    var originalDate by remember { mutableStateOf<LocalDate?>(null) }
    var originalStartTime by remember { mutableStateOf<LocalTime?>(null) }
    var originalEndTime by remember { mutableStateOf<LocalTime?>(null) }

    // Determinar si hay cambios
    val hasChanges = remember {
        derivedStateOf {
            selectedDate != originalDate ||
                    startTime != originalStartTime ||
                    endTime != originalEndTime
        }
    }

    // Cargar datos de la reserva al iniciar
    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                viewModel.cargarReservaParaModificar(id)
                viewModel.reservaSeleccionada.value?.let { reserva ->
                    val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                    val horaInicio = LocalTime.parse(reserva.horaInicio)
                    val horaFin = LocalTime.parse(reserva.horaFin)

                    selectedDate = fecha
                    startTime = horaInicio
                    endTime = horaFin

                    originalDate = fecha
                    originalStartTime = horaInicio
                    originalEndTime = horaFin
                }
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
                        .background(Color(0xFF023E8A)))
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
            // Mostrar mensajes de error
            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "MODIFICAR RESERVA DE RESTAURANTE",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta con información actual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reserva actual:", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        color = Color.White
                    )

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cambiar fecha
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cambiar Fecha", color = Color.White, fontWeight = FontWeight.Bold)
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Botón para guardar cambios
            Button(
                onClick = {
                    reservaId?.let { id ->
                        viewModel.modificarReservaRestauranteCompleta(
                            reservaId = id,
                            nuevaFecha = selectedDate,
                            nuevaHoraInicio = startTime,
                            nuevaHoraFin = endTime,
                            onSuccess = {
                                // Notificar refresco y regresar
                                navController?.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("shouldRefresh", true)
                                navController?.popBackStack()
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasChanges.value) Color(0xFF0077B6) else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = hasChanges.value && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón para regresar
            Button(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("REGRESAR", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Selector de fecha
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        return !date.isBefore(LocalDate.now())
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
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

        // Selector de horario
        if (showTimePicker) {
            val startState = rememberTimePickerState(startTime.hour, startTime.minute)
            val endState = rememberTimePickerState(endTime.hour, endTime.minute)

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Seleccionar Horario") },
                text = {
                    Column {
                        Text("Hora de inicio:")
                        TimePicker(state = startState)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Hora de fin:")
                        TimePicker(state = endState)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        startTime = LocalTime.of(startState.hour, startState.minute)
                        endTime = LocalTime.of(endState.hour, endState.minute)
                        showTimePicker = false
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
fun PreviewModificarReservaRestauranteScreen() {
    ModificarReservaRestauranteScreen(navController = null)
}