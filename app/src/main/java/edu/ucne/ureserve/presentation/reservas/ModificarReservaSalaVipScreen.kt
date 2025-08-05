package edu.ucne.ureserve.presentation.salones

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
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarReservaSalaVipScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var originalDate by remember { mutableStateOf<LocalDate?>(null) }

    // Estado para controlar si hay cambios
    val hasChanges = remember { derivedStateOf { selectedDate != originalDate } }

    // Cargar datos de la reserva al iniciar
    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                viewModel.cargarReservaParaModificar(id)
                viewModel.reservaSeleccionada.value?.let { reserva ->
                    val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                    selectedDate = fecha
                    originalDate = fecha
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_reserve),
                                contentDescription = "Logo",
                                modifier = Modifier.size(60.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Reserva",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "AtrÃ¡s",
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
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "MODIFICAR RESERVA SALA VIP",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Tarjeta con fecha actual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reserva actual:", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Fecha actual: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BotÃ³n seleccionar fecha
            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Seleccionar Nueva Fecha", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BotÃƒÂ³n para guardar cambios (solo habilitado si hay cambios)
            Button(
                onClick = {
                    reservaId?.let { id ->
                        viewModel.modificarReservaRestaurante(
                            onSuccess = {
                                // Notificar a la pantalla anterior que debe refrescar
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
                enabled = hasChanges.value
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Regresar
            Button(
                onClick = { navController?.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("REGRESAR", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Date Picker
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
                DatePicker(
                    state = datePickerState,
                    title = {
                        Text(
                            "Seleccione nueva fecha",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewModificarReservaSalaVipScreen() {
    ModificarReservaSalaVipScreen(navController = rememberNavController())
}
