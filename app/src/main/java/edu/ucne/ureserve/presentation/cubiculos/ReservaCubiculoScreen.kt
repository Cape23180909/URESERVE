package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCubiculoScreen(
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    cubiculoId: Int? = null,
    navController: NavController,
    usuarioDTO: UsuarioDTO,
    estudiante: EstudianteDto
) {
    val cubiculos = viewModel.cubiculos.collectAsState().value
    val hours by viewModel.selectedHours.collectAsState()
    val allMembers by viewModel.members.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var validarCantidaHoras by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(usuarioDTO) {
        viewModel.initializeWithUser(usuarioDTO)
    }

    LaunchedEffect(cubiculoId) {
        viewModel.loadCubiculos()
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
                    Image(painter = painterResource(id = R.drawable.icon_cubiculo), contentDescription = "Icono de Cubículo", modifier = Modifier.size(60.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
        )

        Text(
            text = "Reserva de cubículo #$cubiculoId",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                "Seleccione la cantidad de horas:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Digite la cantidad de horas:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = hours,
                onValueChange = {
                    if (it.isEmpty() || it.all(Char::isDigit)) {
                        viewModel.setSelectedHours(it)
                        validarCantidaHoras = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = validarCantidaHoras
            )

            if (validarCantidaHoras) {
                Text(
                    text = "Debe digitar la hora antes de agregar Integrantes.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Añade los integrantes de tu grupo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(
                    onClick = {
                        if (hours.isBlank()) {
                            validarCantidaHoras = true
                        } else {
                            validarCantidaHoras = false
                            navController.navigate("AgregarEstudiante") {
                                launchSingleTop = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                            }
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
                            text = member.nombres.orEmpty() + " " + member.apellidos.orEmpty(),
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
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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
                Text(
                    text = "VOLVER",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    when {
                        hours.isBlank() -> {
                            validarCantidaHoras = true
                            notificationHandler.showNotification(
                                title = "Campo incompleto",
                                message = "Debe ingresar la cantidad de horas."
                            )
                        }

                        allMembers.size < 3 -> {
                            notificationHandler.showNotification(
                                title = "Faltan miembros",
                                message = "Debe añadir al menos 3 integrantes para finalizar la reserva."
                            )
                        }

                        else -> {
                            try {
                                val cantidadHoras = hours.toInt()
                                val matricula = usuarioDTO.estudiante?.matricula?.trim() ?: ""

                                if (matricula.isBlank()) {
                                    notificationHandler.showNotification(
                                        title = "Matrícula inválida",
                                        message = "Debe añadir una matrícula válida."
                                    )
                                    return@Button
                                }

                                val horaActual = LocalTime.now()
                                val horaInicio = horaActual
                                val horaFin = horaInicio.plusHours(cantidadHoras.toLong())
                                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

                                viewModel.confirmarReservaCubiculo(
                                    cubiculoId = cubiculoId ?: 0,
                                    cantidadHoras = cantidadHoras,
                                    matricula = matricula,
                                    horaInicio = horaInicio.format(formatter),
                                    horaFin = horaFin.format(formatter),
                                    onSuccess = { codigo ->
                                        notificationHandler.showNotification(
                                            title = "Reserva completada",
                                            message = "Tu reserva ha sido finalizada correctamente."
                                        )
                                        navController.navigate("ReservaCubiculoExitosa/$codigo") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    onError = { mensaje ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(mensaje)
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al procesar reserva: ${e.message}")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF2E5C94)).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* TODO: Ir a Inicio */ }) {
                Icon(painter = painterResource(id = R.drawable.icon_inicio), contentDescription = "Inicio", tint = Color.White)
            }
        }
    }
}
