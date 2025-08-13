package edu.ucne.ureserve.presentation.proyectores

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private const val TIME_FORMAT_PATTERN = "hh:mm a"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PrevisualizacionProyectorScreen(
    navController: NavController,
    reservaArgs: ReservaProyectorArgs,
    viewModel: ReservaProyectorViewModel = hiltViewModel()
) {

    val (fecha, horaInicio, horaFin, proyectorJson) = reservaArgs
    val context = LocalContext.current
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null
    val notificationHandler = remember { NotificationHandler(context) }
    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    val proyectorSeleccionadoFromJson = remember(proyectorJson) {
        try {
            proyectorJson?.let { Json.decodeFromString<ProyectoresDto>(it) }?.also {
                viewModel.seleccionarProyector(it)
            }
        } catch (_: Exception) {
            null
        }
    }

    val proyectorSeleccionado by viewModel.proyectorSeleccionado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val proyectorFinal = proyectorSeleccionadoFromJson ?: proyectorSeleccionado

    LaunchedEffect(Unit) {
        if (proyectorFinal == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("No se encontró proyector seleccionado")
                navController.popBackStack()
            }
        }
    }

    val horariosDisponibles = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "05:00 PM"
    ).sortedBy {
        LocalTime.parse(it, DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN, Locale.US))
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    val fechaLocalDate = try {
        LocalDate.parse(fecha, dateFormatter)
    } catch (_: DateTimeParseException) {
        null
    }

    val timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN, Locale.US)
    val horaInicioParsed = try {
        LocalTime.parse(horaInicio, timeFormatter)
    } catch (_: Exception) {
        null
    }
    val horaFinParsed = try {
        LocalTime.parse(horaFin, timeFormatter)
    } catch (_: Exception) {
        null
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(paddingValues)
        ) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF6D87A4))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_reserve),
                            contentDescription = "Logo",
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            text = "Confirmación de Reserva",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            painter = painterResource(id = R.drawable.icon_proyector),
                            contentDescription = "Proyector",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Resumen de tu Reserva",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Fecha",
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fecha: ${fechaLocalDate?.format(dateFormatter) ?: "--"}",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xA4FDD835))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Yellow)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pendiente de confirmar",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = "Horarios seleccionados:",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp)
                    ) {
                        horariosDisponibles.forEach { horario ->
                            val isReserved = isWithinReservation(horario, horaInicio, horaFin)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(if (isReserved) Color(0xFFBDECB6) else Color.White)
                                    .border(width = 1.dp, color = Color.LightGray)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = horario,
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                    if (isReserved) {
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "✔",
                                            color = Color.Green,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF004BBB),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CANCELAR",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        if (proyectorFinal == null) {
                                            snackbarHostState.showSnackbar("No se ha seleccionado un proyector")
                                            return@launch
                                        }

                                        if (fechaLocalDate == null || horaInicioParsed == null || horaFinParsed == null) {
                                            snackbarHostState.showSnackbar("Datos de reserva inválidos")
                                            return@launch
                                        }

                                        viewModel.confirmarReservaProyector(
                                            fechaLocal = fechaLocalDate,
                                            horaInicio = horaInicioParsed,
                                            horaFin = horaFinParsed
                                        )

                                        delay(1000)

                                        if (viewModel.state.value.reservaConfirmada) {
                                            notificationHandler.showNotification(
                                                title = "Reserva Confirmada",
                                                message = "Tu reserva del proyector ha sido registrada."
                                            )
                                            navController.navigate("ReservaExitosa/${viewModel.state.value.codigoReserva}") {
                                                popUpTo("PrevisualizacionProyectorScreen") { inclusive = true }
                                            }
                                        } else {
                                            snackbarHostState.showSnackbar(viewModel.state.value.error ?: "Error desconocido")
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error: ${e.message ?: "Ocurrió un error inesperado"}")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6895D2),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "FINALIZAR",
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

@RequiresApi(Build.VERSION_CODES.O)
fun isWithinReservation(horario: String, horaInicio: String, horaFin: String): Boolean {
    val timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN, Locale.US)
    return try {
        val horarioTime = LocalTime.parse(horario, timeFormatter)
        val inicioTime = LocalTime.parse(horaInicio, timeFormatter)
        val finTime = LocalTime.parse(horaFin, timeFormatter)
        horarioTime >= inicioTime && horarioTime <= finTime
    } catch (_: Exception) {
        false
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Pevisualizacioncreen(context: Context) {
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null
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

data class ReservaProyectorArgs(
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val proyectorJson: String? = null,
    val onBack: () -> Unit = {},
    val onFinish: () -> Unit = {}
)
