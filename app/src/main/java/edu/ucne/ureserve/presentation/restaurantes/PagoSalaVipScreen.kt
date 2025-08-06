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
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagoSalaVipScreen(
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
    var metodoPagoSeleccionado by remember { mutableStateOf(DatosPersonalesSalaVipStore.metodoPagoSeleccionado) }
    val scrollState = rememberScrollState()

    val datosPersonales = DatosPersonalesSalaVipStore.lista
    val codigoReserva = remember { (100000..999999).random() }

    val botonHabilitado by remember {
        derivedStateOf {
            metodoPagoSeleccionado != null && datosPersonales.isNotEmpty()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fecha) {
        viewModel.setFecha(fecha)
    }

    // Observa si la reserva fue confirmada o falló
    LaunchedEffect(uiState.reservaConfirmada) {
        if (uiState.reservaConfirmada) {
            navController.navigate("ReservaRestauranteExitosa?numeroReserva=$codigoReserva") {
                popUpTo("pagoSalaVip") { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_reserve),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Pago Sala VIP",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.comer),
                        contentDescription = "Sala VIP",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Fecha seleccionada: $fecha",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SELECCIONE EL MÉTODO DE PAGO",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF023E8A),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        MetodoPagoSalaVipItem("Efectivo", R.drawable.dinero, metodoPagoSeleccionado == "Efectivo") {
                            metodoPagoSeleccionado = "Efectivo"
                            DatosPersonalesSalaVipStore.metodoPagoSeleccionado = "Efectivo"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste pago en efectivo."
                            )
                            navController.navigate("RegistroReservaSalaVip?fecha=$fecha")
                        }

                        MetodoPagoSalaVipItem("Tarjeta de crédito", R.drawable.credito, metodoPagoSeleccionado == "Tarjeta de crédito") {
                            metodoPagoSeleccionado = "Tarjeta de crédito"
                            DatosPersonalesSalaVipStore.metodoPagoSeleccionado = "Tarjeta de crédito"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste tarjeta de crédito."
                            )
                            navController.navigate("TarjetaCreditoSalaVip?fecha=$fecha")
                        }

                        MetodoPagoSalaVipItem("Transferencia bancaria", R.drawable.trasnferencia, metodoPagoSeleccionado == "Transferencia bancaria") {
                            metodoPagoSeleccionado = "Transferencia bancaria"
                            DatosPersonalesSalaVipStore.metodoPagoSeleccionado = "Transferencia bancaria"
                            notificationHandler.showNotification(
                                title = "Método de Pago",
                                message = "Seleccionaste transferencia bancaria."
                            )
                            navController.navigate("SalaVipTransferencia?fecha=$fecha")
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Reserva Sala VIP", color = Color.Black)
                            Text("RD$ 4,000", color = Color.Black)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Fecha:", color = Color.Black)
                            Text(fecha, color = Color.Black)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("RD$ 4,000", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Correo Electrónico: ${persona.correoElectronico}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Nombres: ${persona.nombre}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Apellidos: ${persona.apellidos}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Teléfono: ${persona.telefono}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Matrícula: ${persona.matricula}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Cédula: ${persona.cedula}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Dirección: ${persona.direccion}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                val datosPersonales = DatosPersonalesSalaVipStore.lista
                                val matricula = datosPersonales.firstOrNull()?.matricula ?: run {
                                    viewModel._uiState.update {
                                        it.copy(errorMessage = "No se encontró matrícula en los datos personales")
                                    }
                                    return@Button
                                }

                                val fechaFormateada = try {
                                    val fechaRaw = viewModel.uiState.value.fecha.ifEmpty {
                                        LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
                                    }
                                    // Validar que la fecha esté en formato correcto
                                    LocalDate.parse(fechaRaw, DateTimeFormatter.ofPattern("d/M/yyyy"))
                                    fechaRaw
                                } catch (e: Exception) {
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
                                }

                                // Establecer hora de inicio y fin para un día completo
                                val horaInicio = "00:00:00"
                                val horaFin = "23:59:59"
                                val cantidadHoras = 24

                                notificationHandler.showNotification(
                                    title = "Reserva Confirmada",
                                    message = "Tu reserva en el restaurante se está procesando."
                                )

                                viewModel.confirmarReservacionSalaVIP(
                                    getLista = { DatosPersonalesSalaVipStore.lista },
                                    getMetodoPagoSeleccionado = { DatosPersonalesSalaVipStore.metodoPagoSeleccionado },
                                    getTarjetaCredito = { DatosPersonalesSalaVipStore.tarjetaCredito },
                                    getDatosPersonales = { DatosPersonalesSalaVipStore.lista.first() },
                                    restauranteId = viewModel.uiState.value.restauranteId ?: 0,
                                    horaInicio = horaInicio,
                                    horaFin = horaFin,
                                    fecha = fechaFormateada,
                                    matricula = matricula,
                                    cantidadHoras = cantidadHoras,
                                    miembros = datosPersonales.map { it.matricula },
                                    tipoReserva = 4
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
                        Text("CONFIRMAR RESERVA", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Método: ${metodoPagoSeleccionado ?: "Ninguno"}, Datos: ${datosPersonales.size}",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MetodoPagoSalaVipItem(
    titulo: String,
    iconRes: Int,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .background(if (seleccionado) Color.LightGray else Color.Transparent)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = titulo,
            tint = Color(0xFF023E8A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = titulo, fontSize = 16.sp, color = Color.Black)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularHoras(horaInicio: String, horaFin: String): Int {
    return try {
        val formato = DateTimeFormatter.ofPattern("HH:mm")
        val inicio = LocalTime.parse(horaInicio, formato)
        val fin = LocalTime.parse(horaFin, formato)
        Duration.between(inicio, fin).toHours().toInt()
    } catch (e: Exception) {
        2 // Valor por defecto
    }
}


data class DatosPersonalesSalaVip(
    val restauranteId: Int? = null,
    val nombre: String = "",
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

object DatosPersonalesSalaVipStore {
    val lista = mutableStateListOf<DatosPersonalesSalaVip>()
    var metodoPagoSeleccionado: String? by mutableStateOf(null)
    var tarjetaCredito: TarjetaCreditoDto? by mutableStateOf(null)
}