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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val NOTIFICACION_TITULO_METODO_PAGO = "Método de Pago"
private const val METODO_PAGO_EFECTIVO = "Efectivo"
private const val METODO_PAGO_TARJETA = "Tarjeta de crédito"
private const val METODO_PAGO_TRANSFERENCIA = "Transferencia bancaria"
private const val FORMATO_FECHA = "d/M/yyyy"
private const val PRECIO_RESTAURANTE = "RD$ 2,500"

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagoRestauranteScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
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

    val uiState by viewModel.uiState.collectAsState()
    var metodoPagoSeleccionado by remember { mutableStateOf(DatosPersonalesRestauranteStore.metodoPagoSeleccionado) }
    val scrollState = rememberScrollState()
    val datosPersonales = DatosPersonalesRestauranteStore.lista
    val codigoReserva = remember { (100000..999999).random() }
    val botonHabilitado by remember { derivedStateOf { metodoPagoSeleccionado != null && datosPersonales.isNotEmpty() } }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fecha) { viewModel.setFecha(fecha) }
    LaunchedEffect(uiState.reservaConfirmada) {
        if (uiState.reservaConfirmada) {
            navController.navigate("ReservaRestauranteExitosa?numeroReserva=$codigoReserva") {
                popUpTo("PagoRestaurante") { inclusive = true }
            }
        }
    }
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
                EncabezadoPagoRestaurante()
                Text(
                    text = "Fecha seleccionada: $fecha",
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                CardMetodosPago(
                    metodoPagoSeleccionado = metodoPagoSeleccionado,
                    onMetodoSeleccionado = { metodo ->
                        metodoPagoSeleccionado = metodo
                        DatosPersonalesRestauranteStore.metodoPagoSeleccionado = metodo
                        notificationHandler.showNotification(
                            title = NOTIFICACION_TITULO_METODO_PAGO,
                            message = "Seleccionaste $metodo."
                        )
                        navController.navigate(getRutaNavegacion(metodo, fecha))
                    }
                )
                Spacer(Modifier.height(16.dp))
                CardResumenPedido(fecha)
                Spacer(Modifier.height(16.dp))
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
                Spacer(Modifier.height(24.dp))
                BotonConfirmarReserva(
                    botonHabilitado = botonHabilitado,
                    uiState = uiState,
                    onClick = { procesarReserva(viewModel, notificationHandler, uiState.fecha, uiState.horaInicio, uiState.horaFin, uiState.restauranteId) }
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Método: ${metodoPagoSeleccionado ?: "Ninguno"}, Datos: ${datosPersonales.size}",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun EncabezadoPagoRestaurante() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.logo_reserve), contentDescription = "Logo", modifier = Modifier.size(40.dp))
        Text(text = "Pago Restaurante", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Image(painter = painterResource(id = R.drawable.comer), contentDescription = "Restaurante", modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun CardMetodosPago(
    metodoPagoSeleccionado: String?,
    onMetodoSeleccionado: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text("SELECCIONE EL MÉTODO DE PAGO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            MetodoPagoItem(METODO_PAGO_EFECTIVO, R.drawable.dinero, metodoPagoSeleccionado == METODO_PAGO_EFECTIVO) {
                onMetodoSeleccionado(METODO_PAGO_EFECTIVO)
            }
            MetodoPagoItem(METODO_PAGO_TARJETA, R.drawable.credito, metodoPagoSeleccionado == METODO_PAGO_TARJETA) {
                onMetodoSeleccionado(METODO_PAGO_TARJETA)
            }
            MetodoPagoItem(METODO_PAGO_TRANSFERENCIA, R.drawable.trasnferencia, metodoPagoSeleccionado == METODO_PAGO_TRANSFERENCIA) {
                onMetodoSeleccionado(METODO_PAGO_TRANSFERENCIA)
            }
        }
    }
}

@Composable
private fun CardResumenPedido(fecha: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Reserva Restaurante", color = Color.Black)
                Text(PRECIO_RESTAURANTE, color = Color.Black)
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Fecha:", color = Color.Black)
                Text(fecha, color = Color.Black)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.Gray)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(PRECIO_RESTAURANTE, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun CardDatosPersonales(persona: DatosPersonalesRestaurante) {
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

@Composable
private fun BotonConfirmarReserva(
    botonHabilitado: Boolean,
    uiState: RestaurantesUiState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = botonHabilitado && !uiState.isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (botonHabilitado && !uiState.isLoading) Color(0xFF0077B6) else Color.Gray,
            contentColor = Color.White
        )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
        } else {
            Text("CONFIRMAR RESERVA", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MetodoPagoItem(titulo: String, iconRes: Int, seleccionado: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .background(if (seleccionado) Color.LightGray else Color.Transparent)
            .padding(8.dp),
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

@RequiresApi(Build.VERSION_CODES.O)
private fun procesarReserva(
    viewModel: RestaurantesViewModel,
    notificationHandler: NotificationHandler,
    fecha: String,
    horaInicio: String,
    horaFin: String,
    restauranteId: Int?
) {
    try {
        val datosPersonales = DatosPersonalesRestauranteStore.lista
        if (datosPersonales.isEmpty()) {
            viewModel._uiState.update {
                it.copy(errorMessage = "No hay datos personales registrados")
            }
            return
        }

        val primeraPersona = datosPersonales.first()
        val matriculaSinFormato = primeraPersona.matricula
        val fechaFormateada = obtenerFechaFormateada(fecha)
        val (horaInicioReserva, horaFinReserva, cantidadHoras) = obtenerHorasReserva(horaInicio, horaFin)

        val params = RestaurantesViewModel.ReservacionParams(
            getLista = { datosPersonales },
            getMetodoPagoSeleccionado = { DatosPersonalesRestauranteStore.metodoPagoSeleccionado },
            getTarjetaCredito = { DatosPersonalesRestauranteStore.tarjetaCredito },
            getDatosPersonales = { primeraPersona },
            restauranteId = restauranteId ?: 0,
            horaInicio = horaInicioReserva,
            horaFin = horaFinReserva,
            fecha = fechaFormateada,
            matricula = matriculaSinFormato,
            cantidadHoras = cantidadHoras,
            miembros = datosPersonales.map { it.matricula },
            tipoReserva = 6
        )

        notificationHandler.showNotification(
            title = "Reserva Confirmada",
            message = "Tu reserva en el restaurante se está procesando."
        )

        viewModel.confirmarReservacionRestaurante(params)
    } catch (e: Exception) {
        viewModel._uiState.update {
            it.copy(errorMessage = "Error al procesar reserva: ${e.localizedMessage}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun obtenerFechaFormateada(fechaRaw: String): String {
    return try {
        val fecha = fechaRaw.ifEmpty {
            LocalDate.now().format(DateTimeFormatter.ofPattern(FORMATO_FECHA))
        }
        LocalDate.parse(fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA))
        fecha
    } catch (_: Exception) {
        LocalDate.now().format(DateTimeFormatter.ofPattern(FORMATO_FECHA))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun obtenerHorasReserva(horaInicio: String, horaFin: String): Triple<String, String, Int> {
    return if (horaInicio.isBlank() || horaFin.isBlank()) {
        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        Triple(horaActual.format(formatter), horaActual.plusHours(24).format(formatter), 24)
    } else {
        Triple(horaInicio, horaFin, calcularHora(horaInicio, horaFin))
    }
}

private fun getRutaNavegacion(metodo: String, fecha: String): String {
    return when (metodo) {
        METODO_PAGO_EFECTIVO -> "RegistroReservaRestaurante?fecha=$fecha"
        METODO_PAGO_TARJETA -> "TarjetaCreditoRestaurante?fecha=$fecha"
        METODO_PAGO_TRANSFERENCIA -> "RestauranteTransferencia?fecha=$fecha"
        else -> "RegistroReservaRestaurante?fecha=$fecha"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun calcularHora(horaInicio: String, horaFin: String): Int {
    return try {
        val inicio = LocalTime.parse(horaInicio)
        val fin = LocalTime.parse(horaFin)
        if (fin.isBefore(inicio)) 24 else ChronoUnit.HOURS.between(inicio, fin).toInt()
    } catch (_: Exception) {
        24
    }
}

fun formatearMatricula(matricula: String): String {
    val limpia = matricula.replace("-", "").replace(" ", "")
    return if (limpia.length == 8 && limpia.all { it.isDigit() }) {
        "${limpia.substring(0, 4)}-${limpia.substring(4)}"
    } else {
        matricula
    }
}

data class DatosPersonalesRestaurante(
    val restauranteId: Int? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val cedula: String = "",
    val matricula: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val correoElectronico: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = ""
)

object DatosPersonalesRestauranteStore {
    val lista = mutableStateListOf<DatosPersonalesRestaurante>()
    var metodoPagoSeleccionado: String? by mutableStateOf(null)
    var tarjetaCredito: TarjetaCreditoDto? by mutableStateOf(null)

}