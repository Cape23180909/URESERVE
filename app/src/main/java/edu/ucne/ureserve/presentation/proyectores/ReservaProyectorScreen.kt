package edu.ucne.ureserve.presentation.proyectores

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun parseFecha(fechaStr: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(fechaStr, formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseHora(horaStr: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return LocalTime.parse(horaStr, formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservaProyectorScreen(
    viewModel: ReservaProyectorViewModel = hiltViewModel(),
    navController: NavController,
    onBottomNavClick: (String) -> Unit = {},
    fecha: String?
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Estados locales para los selectores de hora
    var horaInicio by remember { mutableStateOf("08:00 AM") }
    var horaFin by remember { mutableStateOf("09:00 AM") }
    var expandedInicio by remember { mutableStateOf(false) }
    var expandedFin by remember { mutableStateOf(false) }

    val horas = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "05:00 PM"
    )

    // Efecto para verificar disponibilidad cuando cambian los parámetros
    LaunchedEffect(fecha, horaInicio, horaFin) {
        if (fecha != null) {
            println("Verificando disponibilidad para $horaInicio - $horaFin")
            viewModel.verificarDisponibilidad(fecha, horaInicio, horaFin)
        }
    }

    // Mostrar errores
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                viewModel.limpiarError()
            }
        }
    }

    // Determinar texto y color de disponibilidad
    val (disponibilidadText, disponibilidadColor) = when {
        state.isLoading -> Pair("VERIFICANDO...", Color.Yellow)
        state.disponibilidadVerificada && state.proyectores.isNotEmpty() -> Pair("DISPONIBLE", Color.Green)
        else -> Pair("NO DISPONIBLE", Color.Red)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
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
                            painter = painterResource(id = R.drawable.icon_proyector),
                            contentDescription = "Proyector",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6D87A4)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(paddingValues)
        ) {
            // Sección de disponibilidad
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .border(2.dp, disponibilidadColor, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(disponibilidadColor)
                    )

                    Text(
                        text = disponibilidadText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )

                    Icon(
                        painter = painterResource(R.drawable.icon_proyector),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Selectores de hora
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (fecha != null) {
                    Text(
                        text = "Fecha Seleccionada: $fecha",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Text(
                    text = "Seleccione el horario:",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Selector de hora de inicio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Desde:",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.width(80.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable { expandedInicio = true }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = horaInicio,
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (expandedInicio) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(top = 8.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(horas.size) { index ->
                                val time = horas[index]
                                Button(
                                    onClick = {
                                        horaInicio = time
                                        expandedInicio = false
                                        if (index >= horas.indexOf(horaFin)) {
                                            horaFin = horas.getOrElse(index + 1) { horas.last() }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .width(100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (horaInicio == time) Color(0xFF6895D2) else Color.White,
                                        contentColor = if (horaInicio == time) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = time,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de hora de fin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasta:",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.width(80.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable { expandedFin = true }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = horaFin,
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (expandedFin) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(top = 8.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(horas.size) { index ->
                                val time = horas[index]
                                if (index > horas.indexOf(horaInicio)) {
                                    Button(
                                        onClick = {
                                            horaFin = time
                                            expandedFin = false
                                        },
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .width(100.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (horaFin == time) Color(0xFF6895D2) else Color.White,
                                            contentColor = if (horaFin == time) Color.White else Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = time,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF004BBB)
                    )
                ) {
                    Text("CANCELAR")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (state.proyectores.isNotEmpty()) {
                            coroutineScope.launch {
                                try {
                                    val reservaExitosa = viewModel.crearReserva(
                                        fecha = fecha ?: "",
                                        horaInicio = horaInicio,
                                        horaFin = horaFin,
                                        proyectorId = state.proyectores.first().proyectorId
                                    )

                                    if (reservaExitosa) {
                                        navController.navigate("previsualizacion/${fecha}/${horaInicio}/${horaFin}")
                                    }
                                } catch (e: Exception) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Error al crear reserva: ${e.message}",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.disponibilidadVerificada && state.proyectores.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.disponibilidadVerificada && state.proyectores.isNotEmpty())
                            Color(0xFF6895D2)
                        else
                            Color.Gray
                    )
                ) {
                    Text("CONFIRMAR")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewReservaProyectorScreen() {
    MaterialTheme {
        ReservaProyectorScreen(
            navController = rememberNavController(),
            fecha = "2023-11-15"
        )
    }
}