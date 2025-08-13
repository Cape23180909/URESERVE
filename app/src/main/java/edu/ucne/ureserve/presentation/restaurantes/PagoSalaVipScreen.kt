package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// Constantes para evitar duplicación de literales
private const val METODO_PAGO = "Método de Pago"
private const val TARJETA_CREDITO = "Tarjeta de crédito"
private const val TRANSFERENCIA_BANCARIA = "Transferencia bancaria"
private const val FORMATO_FECHA = "d/M/yyyy"
private const val PRECIO_SALA_VIP = "RD$ 4,000"

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagoSalaVipScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
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
                EncabezadoPagoSalaVip()
                Text(
                    text = "Fecha seleccionada: $fecha",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                CardMetodosPago(
                    metodoPagoSeleccionado = metodoPagoSeleccionado,
                    onMetodoSeleccionado = { metodo ->
                        metodoPagoSeleccionado = metodo
                        DatosPersonalesSalaVipStore.metodoPagoSeleccionado = metodo
                        notificationHandler.showNotification(
                            title = METODO_PAGO,
                            message = "Seleccionaste $metodo."
                        )
                        navController.navigate(getRutaNavegacion(metodo, fecha))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CardResumenPedido(fecha)
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
                        CardDatosPersonales(persona)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                BotonConfirmarReserva(
                    botonHabilitado = botonHabilitado,
                    uiState = uiState,
                    onClick = { procesarReserva(viewModel, notificationHandler) }
                )
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
private fun EncabezadoPagoSalaVip() {
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
}

@Composable
private fun CardMetodosPago(
    metodoPagoSeleccionado: String?,
    onMetodoSeleccionado: (String) -> Unit
) {
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
                onMetodoSeleccionado("Efectivo")
            }
            MetodoPagoSalaVipItem(TARJETA_CREDITO, R.drawable.credito, metodoPagoSeleccionado == TARJETA_CREDITO) {
                onMetodoSeleccionado(TARJETA_CREDITO)
            }
            MetodoPagoSalaVipItem(TRANSFERENCIA_BANCARIA, R.drawable.trasnferencia, metodoPagoSeleccionado == TRANSFERENCIA_BANCARIA) {
                onMetodoSeleccionado(TRANSFERENCIA_BANCARIA)
            }
        }
    }
}

@Composable
private fun CardResumenPedido(fecha: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Reserva Sala VIP", color = Color.Black)
                Text(PRECIO_SALA_VIP, color = Color.Black)
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Fecha:", color = Color.Black)
                Text(fecha, color = Color.Black)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(PRECIO_SALA_VIP, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun CardDatosPersonales(persona: DatosPersonalesSalaVip) {
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

@Composable
private fun BotonConfirmarReserva(
    botonHabilitado: Boolean,
    uiState: RestaurantesUiState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
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
}

private fun getRutaNavegacion(metodo: String, fecha: String): String {
    return when (metodo) {
        "Efectivo" -> "RegistroReservaSalaVip?fecha=$fecha"
        TARJETA_CREDITO -> "TarjetaCreditoSalaVip?fecha=$fecha"
        TRANSFERENCIA_BANCARIA -> "SalaVipTransferencia?fecha=$fecha"
        else -> "RegistroReservaSalaVip?fecha=$fecha"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun procesarReserva(
    viewModel: RestaurantesViewModel,
    notificationHandler: NotificationHandler
) {
    try {
        val datosPersonales = DatosPersonalesSalaVipStore.lista
        if (datosPersonales.isEmpty()) {
            viewModel._uiState.update {
                it.copy(errorMessage = "No hay datos personales registrados")
            }
            return
        }

        val matricula = datosPersonales.first().matricula
        val fechaFormateada = try {
            val fechaRaw = viewModel.uiState.value.fecha.ifEmpty {
                LocalDate.now().format(DateTimeFormatter.ofPattern(FORMATO_FECHA))
            }
            LocalDate.parse(fechaRaw, DateTimeFormatter.ofPattern(FORMATO_FECHA))
            fechaRaw
        } catch (_: Exception) {
            LocalDate.now().format(DateTimeFormatter.ofPattern(FORMATO_FECHA))
        }

        val params = RestaurantesViewModel.ReservacionParams(
            getLista = { DatosPersonalesSalaVipStore.lista },
            getMetodoPagoSeleccionado = { DatosPersonalesSalaVipStore.metodoPagoSeleccionado },
            getTarjetaCredito = { DatosPersonalesSalaVipStore.tarjetaCredito },
            getDatosPersonales = { DatosPersonalesSalaVipStore.lista.first() },
            restauranteId = viewModel.uiState.value.restauranteId ?: 0,
            horaInicio = "00:00:00",
            horaFin = "23:59:59",
            fecha = fechaFormateada,
            matricula = matricula,
            cantidadHoras = 24,
            miembros = datosPersonales.map { it.matricula },
            tipoReserva = 4
        )

        notificationHandler.showNotification(
            title = "Reserva Confirmada",
            message = "Tu reserva en la Sala VIP se está procesando."
        )

        viewModel.confirmarReservacionSalaVIP(params)
    } catch (e: Exception) {
        viewModel._uiState.update {
            it.copy(errorMessage = "Error al procesar reserva: ${e.localizedMessage}")
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
    } catch (_: Exception) {
        2
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