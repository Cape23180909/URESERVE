package edu.ucne.ureserve.presentation.salones

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import edu.ucne.ureserve.presentation.restaurantes.DatosPersonalesSalaVipStore
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel
import kotlinx.coroutines.flow.update
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagoSalonScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var metodoPagoSeleccionado by remember { mutableStateOf(DatosPersonalesSalonStore.metodoPagoSeleccionado) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val datosPersonales = DatosPersonalesSalonStore.lista
    val codigoReserva = remember { (100000..999999).random() }

    val botonHabilitado by remember {
        derivedStateOf { metodoPagoSeleccionado != null && datosPersonales.isNotEmpty() }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fecha) { viewModel.setFecha(fecha) }

    LaunchedEffect(uiState.reservaConfirmada) {
        if (uiState.reservaConfirmada) {
            navController.navigate("ReservaExitosaSalon?numeroReserva=$codigoReserva") {
                popUpTo("pagoSalon") { inclusive = true }
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
                        text = "Pago Salón de Reuniones",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.salon),
                        contentDescription = "Salón",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Fecha seleccionada: $fecha",
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "SELECCIONE EL MÉTODO DE PAGO",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF023E8A),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))

                        MetodoPagoSalonItem("Efectivo", R.drawable.dinero) {
                            metodoPagoSeleccionado = "Efectivo"
                            DatosPersonalesSalonStore.metodoPagoSeleccionado = "Efectivo"
                            navController.navigate("RegistroReservaSalon?fecha=$fecha")
                        }
                        MetodoPagoSalonItem("Tarjeta de crédito", R.drawable.credito) {
                            metodoPagoSeleccionado = "Tarjeta de crédito"
                            DatosPersonalesSalonStore.metodoPagoSeleccionado = "Tarjeta de crédito"
                            navController.navigate("TarjetaCreditoSalon?fecha=$fecha")
                        }
                        MetodoPagoSalonItem("Transferencia bancaria", R.drawable.trasnferencia) {
                            metodoPagoSeleccionado = "Transferencia bancaria"
                            DatosPersonalesSalonStore.metodoPagoSeleccionado = "Transferencia bancaria"
                            navController.navigate("SalonTransferencia?fecha=$fecha")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Reserva Salón de Reuniones", color = Color.Black)
                            Text("RD$ 15,000", color = Color.Black)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Fecha:", color = Color.Black)
                            Text(fecha, color = Color.Black)
                        }
                        Divider(Modifier.padding(vertical = 8.dp), color = Color.Gray)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("RD$ 15,000", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

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
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Correo: ${persona.correoElectronico}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Nombre: ${persona.nombres} ${persona.apellidos}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Teléfono: ${persona.telefono}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Matrícula: ${persona.matricula}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Cédula: ${persona.cedula}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Dirección: ${persona.direccion}", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                // Usar DatosPersonalesSalonStore en lugar de DatosPersonalesSalaVipStore
                                val datosPersonales = DatosPersonalesSalonStore.lista
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

                                val (horaInicio, horaFin, cantidadHoras) = if (
                                    viewModel.uiState.value.horaInicio.isBlank() || viewModel.uiState.value.horaFin.isBlank()
                                ) {
                                    val horaActual = LocalTime.now()
                                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                                    val horasReserva = 2
                                    Triple(
                                        horaActual.format(formatter),
                                        horaActual.plusHours(horasReserva.toLong()).format(formatter),
                                        horasReserva
                                    )
                                } else {
                                    Triple(
                                        viewModel.uiState.value.horaInicio,
                                        viewModel.uiState.value.horaFin,
                                        edu.ucne.ureserve.presentation.restaurantes.calcularHoras(
                                            viewModel.uiState.value.horaInicio,
                                            viewModel.uiState.value.horaFin
                                        )
                                    )
                                }

                                viewModel.confirmarReservacionSalonReuniones(
                                    getLista = { DatosPersonalesSalonStore.lista },
                                    getMetodoPagoSeleccionado = { DatosPersonalesSalonStore.metodoPagoSeleccionado },
                                    getTarjetaCredito = { DatosPersonalesSalonStore.tarjetaCredito },
                                    getDatosPersonales = { DatosPersonalesSalonStore.lista.first() },
                                    restauranteId = viewModel.uiState.value.restauranteId ?: 0,
                                    horaInicio = horaInicio,
                                    horaFin = horaFin,
                                    fecha = fechaFormateada,
                                    matricula = matricula,
                                    cantidadHoras = cantidadHoras,
                                    miembros = datosPersonales.map { it.matricula }
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
fun MetodoPagoSalonItem(titulo: String, iconRes: Int, onClick: () -> Unit) {
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

data class DatosPersonalesSalon(
    val restauranteId: Int? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val cedula: String = "",
    val matricula: String = "",
    val direccion: String = "",
    val capacidad: Int = 0,
    val telefono: String = "",
    val correoElectronico: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = ""
)

object DatosPersonalesSalonStore {
    val lista = mutableStateListOf<DatosPersonalesSalon>()
    var metodoPagoSeleccionado: String? by mutableStateOf(null)
    var tarjetaCredito: TarjetaCreditoDto? by mutableStateOf(null)
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularHoras(horaInicio: String, horaFin: String): Int =
    try {
        val fmt = DateTimeFormatter.ofPattern("HH:mm")
        val ini = LocalTime.parse(horaInicio, fmt)
        val fin = LocalTime.parse(horaFin, fmt)
        Duration.between(ini, fin).toHours().toInt()
    } catch (e: Exception) { 2 }

//@Preview(showBackground = true)
//@Composable
//fun PreviewPagoSalonScreen() {
//    val navController = rememberNavController()
//    PagoSalonScreen(
//        fecha = "20/06/2025",
//        navController = navController
//    )
//}