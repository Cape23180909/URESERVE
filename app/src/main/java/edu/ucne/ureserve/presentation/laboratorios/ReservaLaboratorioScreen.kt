package edu.ucne.ureserve.presentation.laboratorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaLaboratorioScreen(
    viewModel: ReservaLaboratorioViewModel = hiltViewModel(),
    laboratorioId: Int? = null,
    navController: NavController,
    usuarioDTO: UsuarioDTO,
    estudiante: EstudianteDto,
    horaInicio: String,
    horaFin: String,
    fecha: Long
) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }
    val hours by viewModel.selectedHours.collectAsState()
    val allMembers by viewModel.members.collectAsState()
    val laboratorioNombre by viewModel.laboratorioNombre.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val fechaSeleccionada = Calendar.getInstance().apply { timeInMillis = fecha }


    LaunchedEffect(allMembers) {
        Log.d("ReservaLaboratorioScreen", "Miembros actualizados: ${allMembers.size}")
    }

    LaunchedEffect(usuarioDTO) {
        viewModel.initializeWithUser(usuarioDTO)
    }

    LaunchedEffect(laboratorioId) {
        laboratorioId?.let { viewModel.getLaboratorioNombreById(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.logo_reserve), contentDescription = "Logo", modifier = Modifier.size(60.dp))
                    Image(painter = painterResource(id = R.drawable.icon_laboratorio), contentDescription = "Icono", modifier = Modifier.size(60.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
        )

        Text(
            text = "Reserva de $laboratorioNombre",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Horas Seleccionadas:",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Desde: $horaInicio",
                color = Color.White
            )
            Text(
                "Hasta: $horaFin",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Añade los integrantes de tu grupo",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(
                    onClick = {
                        navController.navigate("AgregarEstudianteLaboratorio") {
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_agregarcubicul),
                        contentDescription = "Agregar",
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F4C81), RoundedCornerShape(4.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nombre", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(start = 16.dp))
                    Text("Matrícula", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(end = 16.dp), textAlign = TextAlign.End)
                }
                Divider(color = Color.White, thickness = 1.dp)
                allMembers.forEachIndexed { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.White else Color.LightGray)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${member.nombres.orEmpty()} ${member.apellidos.orEmpty()}",
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = member.estudiante?.matricula.orEmpty(),
                            modifier = Modifier.weight(1f).padding(end = 16.dp),
                            textAlign = TextAlign.End,
                            color = Color.Black
                        )
                    }
                    if (index < allMembers.size - 1) {
                        Divider(color = Color.White, thickness = 1.dp)
                    }
                }
            }

            val faltantes = (3 - allMembers.size).coerceAtLeast(0)
            Text(
                text = if (faltantes > 0)
                    "Faltan $faltantes ${if (faltantes == 1) "miembro" else "miembros"} para completar el mínimo requerido (3)."
                else
                    "Tienes el mínimo requerido (3 miembros).",
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
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
                Text("VOLVER", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            }

            @RequiresApi(Build.VERSION_CODES.O)
            fun calcularHoras(inicio: String, fin: String): Int {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("h:mma", java.util.Locale.US)
                val inicioTime = java.time.LocalTime.parse(inicio, formatter)
                val finTime = java.time.LocalTime.parse(fin, formatter)
                return java.time.Duration.between(inicioTime, finTime).toHours().toInt().coerceAtLeast(1)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            fun convertirA24Horas(hora12: String): String {
                val formatter12 = java.time.format.DateTimeFormatter.ofPattern("h:mma", java.util.Locale.US)
                val formatter24 = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
                return java.time.LocalTime.parse(hora12, formatter12).format(formatter24)
            }

            Button(
                onClick = {
                    if (allMembers.size >= 3) {
                        val matricula = usuarioDTO.estudiante?.matricula ?: ""
                        val cantidadHoras = calcularHorasLaboratorio(horaInicio, horaFin)
                        val horaInicio24 = convertirA24HorasLaboratorio(horaInicio)
                        val horaFin24 = convertirA24HorasLaboratorio(horaFin)
                        val fechaSeleccionadaStr = formatoFechaLaboratorio(fechaSeleccionada)

                        if (fechaSeleccionadaStr.isEmpty() || horaInicio24.isEmpty() || horaFin24.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: Datos de reserva inválidos")
                            }
                            return@Button
                        }

                        viewModel.confirmarReservaLaboratorio(
                            laboratorioId = laboratorioId ?: 0,
                            cantidadHoras = cantidadHoras!!,
                            horaInicio = horaInicio24,
                            horaFin = horaFin24,
                            fecha = fechaSeleccionadaStr,
                            matricula = matricula,
                            onSuccess = { codigo ->
                                Log.d("NAVIGATION", "Código recibido: $codigo")
                                if (codigo > 0) {
                                    try {
                                        notificationHandler.showNotification(
                                            title = "Reserva Exitosa",
                                            message = "¡Has reservado el laboratorio correctamente!"
                                        )
                                        navController.navigate("ReservaLaboratorioExitosa/$codigo") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } catch (e: Exception) {
                                        Log.e("NAVIGATION_ERROR", "Error al navegar: ${e.message}")
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error al navegar a la pantalla de éxito")
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: Código de reserva inválido")
                                    }
                                }
                            },
                            onError = { mensaje ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(mensaje)
                                }
                            }
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Debe tener mínimo 3 miembros.")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6895D2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SIGUIENTE", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E5C94))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* TODO: Ir a Inicio */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_inicio),
                    contentDescription = "Inicio",
                    tint = Color.White
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularHorasLaboratorio(horaInicioTexto: String, horaFinTexto: String): Int? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

        val horaInicio = LocalTime.parse(horaInicioTexto.trim(), formatter)
        val horaFin = LocalTime.parse(horaFinTexto.trim(), formatter)

        val duracion = Duration.between(horaInicio, horaFin)
        duracion.toMinutes().toInt()  // <-- ahora devuelve un Int
    } catch (e: Exception) {
        println("Error al calcular duración: ${e.message}")
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertirA24HorasLaboratorio(hora12: String): String {
    return try {
        val formatter12 = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
        val formatter24 = DateTimeFormatter.ofPattern("HH:mm:ss")
        val time = LocalTime.parse(hora12.trim(), formatter12)
        time.format(formatter24)
    } catch (e: Exception) {
        ""
    }
}
fun formatoFechaLaboratorio(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // ISO 8601
    return dateFormat.format(calendar.time)
}