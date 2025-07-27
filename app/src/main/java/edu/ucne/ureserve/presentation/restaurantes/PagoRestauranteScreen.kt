package edu.ucne.ureserve.presentation.restaurantes

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import kotlinx.coroutines.flow.update

@Composable
fun PagoRestauranteScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val botonHabilitado = DatosPersonalesRestauranteStore.lista.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp)
    ) {
        // Top Bar
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
                text = "Pago Restaurante",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.comer),
                contentDescription = "Restaurante",
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

        // Métodos de pago
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

                MetodoPagoItem("Efectivo", R.drawable.dinero) {
                    DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Efectivo"
                    navController.navigate("RegistroReservaRestaurante?fecha=$fecha")
                }
                MetodoPagoItem("Tarjeta de crédito", R.drawable.credito) {
                    DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Tarjeta de crédito"
                    navController.navigate("TarjetaCreditoRestaurante?fecha=$fecha")
                }
                MetodoPagoItem("Transferencia bancaria", R.drawable.trasnferencia) {
                    DatosPersonalesRestauranteStore.metodoPagoSeleccionado = "Transferencia bancaria"
                    navController.navigate("RestauranteTransferencia?fecha=$fecha")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A))
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Reserva Restaurante", color = Color.Black)
                    Text("RD$ 2,500", color = Color.Black)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Fecha:", color = Color.Black)
                    Text(fecha, color = Color.Black)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("TOTAL", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text("RD$ 2,500", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        if (DatosPersonalesRestauranteStore.lista.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "DATOS PERSONALES REGISTRADOS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DatosPersonalesRestauranteStore.lista.forEach { persona ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Correo Electrónico: ${persona.correoElectronico}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Nombres: ${persona.nombres}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                        val datosPersonales = DatosPersonalesRestauranteStore.lista
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
                                calcularHoras(
                                    viewModel.uiState.value.horaInicio,
                                    viewModel.uiState.value.horaFin
                                )
                            )
                        }

                        viewModel.confirmarReservacionRestaurante(
                            getLista = { DatosPersonalesRestauranteStore.lista },
                            getMetodoPagoSeleccionado = { DatosPersonalesRestauranteStore.metodoPagoSeleccionado },
                            getTarjetaCredito = { DatosPersonalesRestauranteStore.tarjetaCredito },
                            getDatosPersonales = { DatosPersonalesRestauranteStore.lista.first() },
                            restauranteId = viewModel.uiState.value.restauranteId ?: 0,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            fecha = fechaFormateada,
                            matricula = matricula,
                            cantidadHoras = cantidadHoras,
                            miembros = datosPersonales.map { it.matricula }
                        )
                        navController.navigate("ReservaRestauranteExitosa?numeroReserva=RES-${System.currentTimeMillis().toString().takeLast(6)}") {
                            popUpTo("Dashboard") { inclusive = false }
                        }

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
}

// COMPONENTE EXTRA QUE ESTABA DENTRO (AHORA FUERA)

@Composable
fun MetodoPagoItem(titulo: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = titulo,
            tint = Color(0xFF023E8A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = titulo, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPagoRestauranteScreen() {
    val navController = rememberNavController()
    PagoRestauranteScreen(
        fecha = "2025-06-29",
        navController = navController
    )
}

// DATOS PERSONALES (puedes mover esto a otro archivo si quieres)
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