package edu.ucne.ureserve.presentation.empleados

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservasenCursoRestauranteScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getReservasRestaurante()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { HeaderSection() }

        when (state) {
            is ReservaViewModel.ReservaListState.Loading -> {
                item { LoadingSection() }
            }
            is ReservaViewModel.ReservaListState.Success -> {
                item { TableHeader() }
                items(reservaciones) { reserva ->
                    ReservationRestauranteItem(
                        reserva = reserva,
                        color = tipoReservaColor(reserva.tipoReserva),
                        onClick = {
                            val encodedMatricula = Uri.encode(reserva.matricula)
                            navController.navigate(
                                "detalleReservaRestaurante/${reserva.codigoReserva}/${reserva.fecha}/${reserva.horaInicio}/${reserva.horaFin}/$encodedMatricula/${reserva.tipoReserva}"
                            )
                        }
                    )
                }
            }
            is ReservaViewModel.ReservaListState.Error -> {
                item { ErrorSection((state as ReservaViewModel.ReservaListState.Error).message) }
            }
        }

        item { BackButton { navController.popBackStack() } }
    }
}

@Composable
private fun HeaderSection() {
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
        text = "Reservas de Restaurantes en Curso",
        fontSize = 23.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun LoadingSection() {
    Text(
        text = "Cargando reservas...",
        color = Color.White,
        fontSize = 18.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TableHeader() {
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

@Composable
private fun ErrorSection(message: String) {
    Text(
        text = message,
        color = Color.Red,
        fontSize = 18.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(20.dp))
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
    ) {
        Text("Volver", color = Color.White, fontSize = 18.sp)
    }
}

@Composable
fun ReservationRestauranteItem(
    reserva: ReservacionesDto,
    color: Color,
    onClick: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf("Cargando...") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(reserva.fecha) {
        val fechaHoraFin = parseFechaHoraFin(reserva.fecha)
        if (fechaHoraFin == null) {
            error = true
            tiempoRestante = "--:--"
            return@LaunchedEffect
        }
        while (true) {
            val ahora = System.currentTimeMillis()
            val diff = fechaHoraFin.time - ahora
            tiempoRestante = if (diff > 0) formatearTiempoRestante(diff) else "Finalizado"
            if (diff <= 0) break
            delay(1000L - (System.currentTimeMillis() % 1000))
        }
    }

    val isActive = tiempoRestante != "Finalizado" && !error
    val iconRes = tipoReservaIcon(reserva.tipoReserva)
    val tipoReservaText = tipoReservaTexto(reserva.tipoReserva)

    ReservationItemContent(
        tiempoRestante = tiempoRestante,
        isActive = isActive,
        iconRes = iconRes,
        tipoReservaText = tipoReservaText,
        color = color,
        onClick = onClick
    )
}

@Composable
private fun ReservationItemContent(
    tiempoRestante: String,
    isActive: Boolean,
    iconRes: Int,
    tipoReservaText: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(if (isActive) Color.White else Color.LightGray)
                .widthIn(min = 100.dp)
                .padding(8.dp)
        ) {
            Text(
                text = tiempoRestante,
                fontSize = 16.sp,
                color = if (isActive) Color.Black else Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(if (isActive) color else Color.Gray)
                .widthIn(min = 200.dp)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Tipo de Reserva",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(
                        text = tipoReservaText,
                        fontSize = 14.sp,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isActive) "Ahora" else "Finalizado",
                        fontSize = 12.sp,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "00:00 a 23:59",
                        fontSize = 12.sp,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun parseFechaHoraFin(fecha: String): Date? {
    return try {
        val fechaLimpia = fecha.take(10)
        val horaFin = "23:59:59"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.parse("$fechaLimpia $horaFin")
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
    return String.format(Locale.getDefault(), "%02dh %02dmin %02ds", horas, minutosRestantes, segundosRestantes)
}

private fun tipoReservaColor(tipoReserva: Int): Color = when (tipoReserva) {
    4 -> Color(0xFFFFA500)
    5 -> Color(0xFFADD8E6)
    else -> Color(0xFF6EE610)
}

private fun tipoReservaIcon(tipoReserva: Int): Int = when (tipoReserva) {
    4 -> R.drawable.sala
    5 -> R.drawable.salon
    else -> R.drawable.icon_restaurante
}

private fun tipoReservaTexto(tipoReserva: Int): String = when (tipoReserva) {
    4 -> "Sala VIP"
    5 -> "Salón de Reuniones"
    else -> "Restaurante"
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewReservasenCursoRestauranteScreen() {
    val navController = rememberNavController()
    ReservasenCursoRestauranteScreen(navController = navController)
}