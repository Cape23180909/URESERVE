package edu.ucne.ureserve.presentation.proyectores

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.presentation.login.AuthManager
import edu.ucne.ureserve.presentation.proyectores.DateTimeUtils.parseHora
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PrevisualizacionProyectorScreen(
    viewModel: ReservaProyectorViewModel = hiltViewModel(),
    navController: NavController,
    fecha: String,
    horaInicio: String,
    horaFin: String,
    proyectorJson: String? = null,
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {}
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
    // Procesar el proyector desde el JSON
    val proyectorSeleccionadoFromJson = remember(proyectorJson) {
        try {
            proyectorJson?.let { Json.decodeFromString<ProyectoresDto>(it) }?.also {
                viewModel.seleccionarProyector(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtener el estado del ViewModel
    val state by viewModel.state.collectAsState()
    val proyectorSeleccionado by viewModel.proyectorSeleccionado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Usar el proyector de cualquiera de las dos fuentes
    val proyectorFinal = proyectorSeleccionadoFromJson ?: proyectorSeleccionado

    // Verificar que tenemos un proyector
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
    )

    // Formateador para fechas
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())

    // Parsear la fecha desde String a LocalDate
    val fechaLocalDate = try {
        LocalDate.parse(fecha, dateFormatter)
    } catch (e: DateTimeParseException) {
        null
    }

    val (horaInicioParsed, horaFinParsed) = try {
        val horaInicioNormalizada = if (horaInicio.contains(":")) horaInicio else {
            horaInicio.replace(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        }

        val horaFinNormalizada = if (horaFin.contains(":")) horaFin else {
            horaFin.replace(Regex("(\\d{2})(\\d{2})"), "$1:$2")
        }

        Pair(parseHora(horaInicioNormalizada), parseHora(horaFinNormalizada))
    } catch (e: Exception) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                "Error en formato de hora: ${e.message ?: "Use formato HH:MM AM/PM"}"
            )
        }
        Pair(null, null)
    }

    val codigoReserva = remember { (100000..999999).random() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(paddingValues)
        ) {
            item {
                Column {
                    // Encabezado
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

                    // Resumen de la reserva
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
                            modifier = Modifier.padding(bottom = 16.dp))

                        // Detalles del proyector

                        // Fecha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Fecha",
                                modifier = Modifier.size(30.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fecha: ${fechaLocalDate?.format(dateFormatter) ?: "--"}",
                                color = Color.Black,
                                fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Estado
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
                                    .background(Color.Yellow))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pendiente de confirmar",
                                color = Color.Black,
                                fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Lista horarios seleccionados
                    Text(
                        text = "Horarios seleccionados:",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp)
                    ) {
                        horariosDisponibles.forEach { horario ->
                            val isReserved = isWithinReservation(horario, horaInicio, horaFin, horariosDisponibles)
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
                                        fontSize = 16.sp)
                                    if (isReserved) {
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "✔",
                                            color = Color.Green,
                                            fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Botones inferiores
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
                                modifier = Modifier.padding(8.dp))
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        if (proyectorFinal == null) {
                                            snackbarHostState.showSnackbar("No se ha seleccionado un proyector")
                                            return@launch
                                        }

                                        val matricula = AuthManager.currentUser?.correoInstitucional ?: run {
                                            snackbarHostState.showSnackbar("Usuario no autenticado")
                                            return@launch
                                        }

                                        if (fechaLocalDate == null) {
                                            snackbarHostState.showSnackbar("Fecha inválida")
                                            return@launch
                                        }

                                        // Validar horas
                                        try {
                                            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                                            val horaInicioTime = LocalTime.parse(horaInicio, timeFormatter)
                                            val horaFinTime = LocalTime.parse(horaFin, timeFormatter)

                                            viewModel.confirmarReservaProyector(
                                                proyectorId = proyectorFinal.proyectorId,
                                                fechaLocal = fechaLocalDate!!,
                                                horaInicio = horaInicioTime,
                                                horaFin = horaFinTime,
                                                matricula = matricula
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
                                            }
                                            else {
                                                snackbarHostState.showSnackbar(viewModel.state.value.error ?: "Error desconocido")
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Formato de hora inválido")
                                            return@launch
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

// Función auxiliar para determinar si un horario está dentro del rango reservado
fun isWithinReservation(horario: String, horaInicio: String, horaFin: String, horarios: List<String>): Boolean {
    val inicioIndex = horarios.indexOf(horaInicio)
    val finIndex = horarios.indexOf(horaFin)
    val currentIndex = horarios.indexOf(horario)
    return if (inicioIndex == -1 || finIndex == -1 || currentIndex == -1) {
        false
    } else {
        currentIndex in inicioIndex..finIndex
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Pevisualizacioncreen(context: Context) {
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
fun PreviewPrevisualizacionProyectorScreen() {
    MaterialTheme {
        PrevisualizacionProyectorScreen(
            navController = rememberNavController(),
            fecha = "24-06-2025",
            horaInicio = "12:00 PM",
            horaFin = "01:00 PM"
        )
    }
}