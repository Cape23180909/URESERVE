package edu.ucne.ureserve.presentation.proyectores

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.proyectores.DateTimeUtils.formatHora
import edu.ucne.ureserve.presentation.proyectores.DateTimeUtils.parseFecha
import edu.ucne.ureserve.presentation.proyectores.DateTimeUtils.parseHora
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReservaProyectorScreen(
    viewModel: ReservaProyectorViewModel = hiltViewModel(),
    navController: NavController,
    onBottomNavClick: (String) -> Unit = {}
) {
    val context = LocalContext.current


    // Solicitud de permiso para notificaciones en Android 13+
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else null

    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    val fechaActual by remember { mutableStateOf(viewModel.obtenerFechaActual()) }
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var horaInicio by remember { mutableStateOf("08:00 AM") }
    var horaFin by remember { mutableStateOf("09:00 AM") }
    var expandedInicio by remember { mutableStateOf(false) }
    var expandedFin by remember { mutableStateOf(false) }

    val horas = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "05:00 PM"
    )

    val fechaParaVerificacion by remember { mutableStateOf(fechaActual) }

    LaunchedEffect(horaInicio, horaFin, fechaParaVerificacion) {
        if (fechaParaVerificacion.isNotBlank()) {
            viewModel.verificarDisponibilidad(fechaParaVerificacion, horaInicio, horaFin)
        } else {
            viewModel.limpiarError()
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Fecha de verificación no disponible.")
            }
        }
    }

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

    val (disponibilidadText, disponibilidadColor) = when {
        state.isLoading -> Pair("VERIFICANDO...", Color.Yellow)
        state.proyectores.any { it.disponible } -> Pair("DISPONIBLE", Color.Green)
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

            Text(
                text = "Fecha: $fechaActual",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Seleccione el horario:",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de selección de hora de inicio
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
                                val timeAmPm = horas[index]
                                Button(
                                    onClick = {
                                        horaInicio = timeAmPm
                                        expandedInicio = false

                                        //  Notificación al seleccionar la hora de inicio
                                        notificationHandler.showNotification(
                                            title = "Hora seleccionada",
                                            message = "Seleccionaste la hora de inicio: $horaInicio"
                                        )

                                        // Asegurar que horaFin sea posterior a horaInicio
                                        if (horas.indexOf(horaFin) <= index) {
                                            horaFin = horas.getOrElse(index + 1) { horas.last() }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .width(100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (horaInicio == timeAmPm) Color(0xFF6895D2) else Color.White,
                                        contentColor = if (horaInicio == timeAmPm) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = timeAmPm,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de selección de hora de fin
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
                                val timeAmPm = horas[index]
                                if (index > horas.indexOf(horaInicio)) {
                                    Button(
                                        onClick = {
                                            horaFin = timeAmPm
                                            expandedFin = false
                                        },
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .width(100.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (horaFin == timeAmPm) Color(0xFF6895D2) else Color.White,
                                            contentColor = if (horaFin == timeAmPm) Color.White else Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = timeAmPm,
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

            // Botones inferiores
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
                        if (state.proyectores.any { it.disponible }) {
                            coroutineScope.launch {
                                try {
                                    val horaInicioParsed = parseHora(horaInicio)
                                    val horaFinParsed = parseHora(horaFin)
                                    if (horaInicioParsed.isAfter(horaFinParsed)) {
                                        snackbarHostState.showSnackbar("La hora final debe ser después de la inicial")
                                        return@launch
                                    }

                                    // Seleccionar el primer proyector disponible
                                    val proyectorSeleccionado = state.proyectores.first { it.disponible }
                                    viewModel.seleccionarProyector(proyectorSeleccionado)

                                    if (viewModel.proyectorSeleccionado.value == null) {
                                        snackbarHostState.showSnackbar("Error al seleccionar el proyector")
                                        return@launch
                                    }

                                    val fechaParseada = parseFecha(fechaActual)
                                    val fechaStrFormatted = fechaParseada.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    val proyectorJson = Uri.encode(Json.encodeToString(proyectorSeleccionado))

                                    notificationHandler.showNotification(
                                        title = "Reserva Confirmada",
                                        message = "Tu reserva del proyector ha sido registrada."
                                    )

                                    // Navegar
                                    navController.navigate(
                                        "previsualizacion/${fechaStrFormatted}/" +
                                                "${formatHora(horaInicioParsed)}/${formatHora(horaFinParsed)}/$proyectorJson"
                                    ) {
                                        launchSingleTop = true
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message ?: "Ocurrió un error"}")
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No hay proyectores disponibles")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.proyectores.any { it.disponible } && horas.indexOf(horaInicio) < horas.indexOf(horaFin),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.proyectores.any { it.disponible } && horas.indexOf(horaInicio) < horas.indexOf(horaFin))
                            Color(0xFF6895D2) else Color.Gray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONFIRMAR")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(context: Context) {
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else {
            null
        }

    val notificationHandler = NotificationHandler(context)

    LaunchedEffect(key1 = true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Button(onClick = {
                notificationHandler.showNotification(
                    title = "Reserva Confirmada",
                    message = "Tu reserva del proyector ha sido registrada."
                )
            }) {
                Text(text = "Mostrar Notificación")
            }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ReservaProyectorScreenPreview() {
    MaterialTheme {
        ReservaProyectorScreen(
            navController = rememberNavController()
        )
    }
}