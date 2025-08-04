package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import kotlinx.coroutines.flow.update
import java.time.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagoRestauranteScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
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
    val uiState by viewModel.uiState.collectAsState()
    var metodoPagoSeleccionado by remember { mutableStateOf(DatosPersonalesRestauranteStore.metodoPagoSeleccionado) }
    val scrollState = rememberScrollState()
    val datosPersonales = DatosPersonalesRestauranteStore.lista
    val codigoReserva = remember { "RES-${(100000..999999).random()}" }
    val botonHabilitado by remember {
        derivedStateOf { metodoPagoSeleccionado != null && datosPersonales.isNotEmpty() }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fecha) { viewModel.setFecha(fecha) }

    // Navegación tras éxito
    LaunchedEffect(uiState.reservaConfirmada) {
        if (uiState.reservaConfirmada) {
            navController.navigate("ReservaRestauranteExitosa?numeroReserva=$codigoReserva") {
                popUpTo("pagoRestaurante") { inclusive = true }
            }
        }
    }

    // Mostrar errores
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Encabezado
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.logo_reserve), contentDescription = "Logo", modifier = Modifier.size(40.dp))
                    Text(text = "Pago Restaurante", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Image(painter = painterResource(id = R.drawable.comer), contentDescription = "Restaurante", modifier = Modifier.size(40.dp))
                }

                Text(text = "Fecha seleccionada: $fecha", color = Color.White, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))

                // Método de pago
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("SELECCIONE EL MÉTODO DE PAGO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        MetodoPagoRestauranteItem("Efectivo", R.drawable.dinero) {
                            metodoPagoSeleccionado = "Efectivo"
                            DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Efectivo"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste pago en efectivo."
                            )
                            navController.navigate("RegistroReservaRestaurante?fecha=$fecha")
                        }

                        MetodoPagoRestauranteItem("Tarjeta de crédito", R.drawable.credito) {
                            metodoPagoSeleccionado = "Tarjeta de crédito"
                            DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Tarjeta de crédito"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste tarjeta de crédito."
                            )
                            navController.navigate("TarjetaCreditoRestaurante?fecha=$fecha")
                        }

                        MetodoPagoRestauranteItem("Transferencia bancaria", R.drawable.trasnferencia) {
                            metodoPagoSeleccionado = "Transferencia bancaria"
                            DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Transferencia bancaria"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste transferencia bancaria."
                            )
                            navController.navigate("RestauranteTransferencia?fecha=$fecha")
                        }

                    }
                }

                Spacer(Modifier.height(16.dp))

                // Resumen
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Reserva Restaurante", color = Color.Black)
                            Text("RD$ 2,500", color = Color.Black)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Fecha:", color = Color.Black)
                            Text(fecha, color = Color.Black)
                        }
                        Divider(Modifier.padding(vertical = 8.dp), color = Color.Gray)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("RD$ 2,500", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Datos personales
                if (datosPersonales.isNotEmpty()) {
                    Text(
                        text = "DATOS PERSONALES REGISTRADOS",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    datosPersonales.forEach { persona ->
                        Card(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Correo: ${persona.correoElectronico}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Nombre: ${persona.nombres} ${persona.apellidos}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Teléfono: ${persona.telefono}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Matrícula: ${formatearMatricula(persona.matricula)}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Cédula: ${persona.cedula}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Dirección: ${persona.direccion}", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Botón confirmar
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                val datosPersonales = DatosPersonalesRestauranteStore.lista
                                val matriculaSinFormato = datosPersonales.firstOrNull()?.matricula
                                    ?: run {
                                        viewModel._uiState.update {
                                            it.copy(errorMessage = "No se encontró matrícula en los datos personales")
                                        }
                                        return@Button
                                    }

                                // Formatear matrícula
                                val matriculaFormateada = formatearMatricula(matriculaSinFormato)
                                val matriculaParaApi = matriculaFormateada

                                val fechaFormateada = try {
                                    val fechaRaw = uiState.fecha.ifEmpty {
                                        LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
                                    }
                                    LocalDate.parse(fechaRaw, DateTimeFormatter.ofPattern("d/M/yyyy"))
                                    fechaRaw
                                } catch (e: Exception) {
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
                                }

                                // Lógica corregida para 24 horas
                                val (horaInicio, horaFin, cantidadHoras) = if (uiState.horaInicio.isBlank() || uiState.horaFin.isBlank()) {
                                    val horaActual = LocalTime.now()
                                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                                    Triple(
                                        horaActual.format(formatter),
                                        horaActual.plusHours(24).format(formatter),
                                        24 // Cambiado a 24 horas
                                    )
                                } else {
                                    // Si se especifican horas manualmente, calcular la diferencia
                                    val horasCalculadas = calcularHoras(uiState.horaInicio, uiState.horaFin)
                                    Triple(
                                        uiState.horaInicio,
                                        uiState.horaFin,
                                        horasCalculadas
                                    )
                                }

                                notificationHandler.showNotification(
                                    title = "Reserva Confirmada",
                                    message = "Tu reserva en el restaurante se está procesando."
                                )

                                viewModel.confirmarReservacionRestaurante(
                                    getLista = { DatosPersonalesRestauranteStore.lista },
                                    getMetodoPagoSeleccionado = { DatosPersonalesRestauranteStore.metodoPagoSeleccionado },
                                    getTarjetaCredito = { DatosPersonalesRestauranteStore.tarjetaCredito },
                                    getDatosPersonales = { DatosPersonalesRestauranteStore.lista.first() },
                                    restauranteId = uiState.restauranteId ?: 0,
                                    horaInicio = horaInicio,
                                    horaFin = horaFin,
                                    fecha = fechaFormateada,
                                    matricula = matriculaParaApi,
                                    cantidadHoras = cantidadHoras,
                                    miembros = datosPersonales.map { it.matricula },
                                    tipoReserva = 6 // Tipo reserva para restaurante
                                )
                            } catch (e: Exception) {
                                viewModel._uiState.update {
                                    it.copy(errorMessage = "Error al procesar reserva: ${e.localizedMessage}")
                                }
                            }
                        } else {
                            viewModel._uiState.update {
                                it.copy(errorMessage = "Se requiere Android 8.0 o superior")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = botonHabilitado && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (botonHabilitado && !uiState.isLoading) Color(0xFF0077B6) else Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "CONFIRMAR RESERVA",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Método: ${metodoPagoSeleccionado ?: "Ninguno"}, Datos: ${datosPersonales.size}",
                color = Color.White
            )
        }
    }
}

@Composable
fun MetodoPagoRestauranteItem(titulo: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = titulo,
            tint = Color(0xFF023E8A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(titulo, fontSize = 16.sp, color = Color.Black)
    }
}

fun formatearMatricula(matricula: String): String {
    val limpia = matricula.replace("-", "").replace(" ", "")
    return if (limpia.length == 8 && limpia.all { it.isDigit() }) {
        "${limpia.substring(0, 4)}-${limpia.substring(4)}"
    } else {
        matricula // devuelve sin cambios si no es válida
    }
}

data class DatosPersonalesRestaurante(
    val restauranteId: Int? = 0,
    val nombres: String = "",
    val apellidos: String = "",
    val cedula: String = "",
    val matricula: String = "",
    val direccion: String = "",
    val capacidad: Int = 0,
    val telefono: String = "",
    val correoElectronico: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = ""
)

object DatosPersonalesRestauranteStore {
    val lista = mutableStateListOf<DatosPersonalesRestaurante>()
    var metodoPagoSeleccionado: String? by mutableStateOf(null)
    var tarjetaCredito: TarjetaCreditoDto? by mutableStateOf(null)
}

// Preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewPagoRestauranteScreen() {
    val navController = rememberNavController()
    PagoRestauranteScreen(
        fecha = "20/06/2025",
        navController = navController
    )
}