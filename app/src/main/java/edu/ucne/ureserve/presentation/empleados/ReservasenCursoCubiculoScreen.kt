package edu.ucne.ureserve.presentation.empleados

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservasenCursoCubiculoScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
){
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCubiculoReservas()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFA7A7A7))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_reserve),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.icon_reserva),
                    contentDescription = "Reservas en Curso",
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reservas de Cubiculos en Curso",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val currentState = state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    Text(
                        text = "Cargando reservas...",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is ReservaViewModel.ReservaListState.Success -> {
                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2E5C94))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tiempo Restante", color = Color.White)
                                Text("Reservas", color = Color.White)
                            }
                        }

                        items(reservaciones.filterNot { isReservaFinalizada(it.fecha, it.horaFin) }) { reserva ->
                            CubiculoReservationItem(
                                horaInicio = reserva.horaInicio,
                                horaFin = reserva.horaFin,
                                fecha = reserva.fecha,
                                color = Color(0xFF6EE610),
                                onClick = {
                                    navController.navigate(
                                        "detalleReservaCubiculo/${reserva.codigoReserva}/${reserva.fecha}/${reserva.horaInicio}/${reserva.horaFin}/${reserva.matricula}"
                                    )
                                }
                            )
                        }
                    }
                }

                is ReservaViewModel.ReservaListState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
            ) {
                Text("Volver", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

fun calcularTiempoRestante(horaFin: String): Pair<String, Boolean> {
    return try {
        val formato = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val horaFinal = formato.parse(horaFin)
        val ahora = System.currentTimeMillis()

        if (horaFinal != null && horaFinal.time > ahora) {
            val diferencia = horaFinal.time - ahora
            val minutos = diferencia / (60 * 1000)
            val horas = minutos / 60
            val minutosRestantes = minutos % 60

            val tiempoRestante = when {
                horas > 0 -> "${horas}h ${minutosRestantes}min"
                else -> "${minutosRestantes}min"
            }

            Pair(tiempoRestante, true)
        } else {
            Pair("Finalizado", false)
        }
    } catch (e: Exception) {
        Pair("--:--", false)
    }
}

fun isReservaFinalizada(fecha: String, horaFin: String): Boolean {
    val fechaHora = parsearFechaHoraSeguro(fecha, horaFin)
    return fechaHora?.time ?: 0 < System.currentTimeMillis()
}

@Composable
fun CubiculoReservationItem(
    horaInicio: String,
    horaFin: String,
    fecha: String,
    color: Color,
    onClick: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(fecha, horaFin) {
        val fechaHoraFin = parsearFechaHoraSeguro(fecha, horaFin)
        if (fechaHoraFin == null) {
            error = true
            return@LaunchedEffect
        }
        while (true) {
            val ahora = System.currentTimeMillis()
            val diff = fechaHoraFin.time - ahora
            if (diff > 0) {
                tiempoRestante = formatearTiempoRestante(diff)
            } else {
                tiempoRestante = "Finalizado"
                break
            }
            delay(1000L - (System.currentTimeMillis() % 1000))
        }
    }

    val tiempoMostrado = tiempoRestante ?: if (error) "--:--" else "Cargando..."
    val isActive = tiempoMostrado != "Finalizado" && !error
    val colorFondo = if (isActive) color else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Hacer que el Row sea clickeable
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(if (isActive) Color.White else Color.LightGray)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = tiempoMostrado,
                fontSize = 16.sp,
                color = if (isActive) Color.Black else Color.Gray
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(colorFondo)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.icon_cubiculo),
                    contentDescription = "Reserva",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Reservación", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Ahora", fontSize = 12.sp, color = Color.Black)
                    Text(
                        text = "$horaInicio a $horaFin",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun parsearFechaHoraSeguro(fecha: String, hora: String): Date? {
    return try {
        val fechaLimpia = fecha.take(10) // Asegura formato yyyy-MM-dd
        val horaLimpia = hora.take(5)    // Asegura formato HH:mm
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.parse("$fechaLimpia $horaLimpia")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun formatearTiempoRestante(diferencia: Long): String {
    val segundos = diferencia / 1000
    val minutos = segundos / 60
    val horas = minutos / 60
    val minutosRestantes = minutos % 60
    val segundosRestantes = segundos % 60

    return String.format("%02dh %02dmin %02ds", horas, minutosRestantes, segundosRestantes)
}