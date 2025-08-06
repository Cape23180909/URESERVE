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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra gris superior
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

            // Título
            Text(
                text = "Reservas de restaurantes en Curso",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            // Contenido principal
            when (state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    Text(
                        text = "Cargando reservas...",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )
                }
                is ReservaViewModel.ReservaListState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2E5C94))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Tiempo Restante", color = Color.White)
                                    Text(text = "Reservas", color = Color.White)
                                }
                            }
                        }
                        items(reservaciones) { reserva ->
                            ReservationRestauranteItem(
                                reserva = reserva,
                                color = when (reserva.tipoReserva) {
                                    4 -> Color(0xFFFFA500)  // Naranja SalaVIP
                                    5 -> Color(0xFFADD8E6)  // Azul SalaReuniones
                                    else -> Color(0xFF6EE610)  // Verde Restaurante
                                },
                                onClick = {
                                    val encodedMatricula = Uri.encode(reserva.matricula)
                                    navController.navigate(
                                        "detalleReservaRestaurante/${reserva.codigoReserva}/${reserva.fecha}/${reserva.horaInicio}/${reserva.horaFin}/$encodedMatricula/${reserva.tipoReserva}"
                                    )
                                }
                            )
                        }
                    }
                }
                is ReservaViewModel.ReservaListState.Error -> {
                    Text(
                        text = (state as ReservaViewModel.ReservaListState.Error).message,
                        fontSize = 18.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
            ) {
                Text(
                    text = "Volver",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ReservationRestauranteItem(
    reserva: ReservacionesDto,
    color: Color,
    onClick: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf<String>("Cargando...") }
    var error by remember { mutableStateOf(false) }

    fun parsearFechaHoraSeguro(fecha: String, hora: String): Date? {
        return try {
            val fechaLimpia = fecha.take(10) // yyyy-MM-dd
            val horaLimpia = hora.take(8) // HH:mm:ss
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.parse("$fechaLimpia $horaLimpia")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatearTiempoRestante(diferencia: Long): String {
        val segundos = diferencia / 1000
        val minutos = segundos / 60
        val horas = minutos / 60
        val minutosRestantes = minutos % 60
        val segundosRestantes = segundos % 60
        return String.format("%02dh %02dmin %02ds", horas, minutosRestantes, segundosRestantes)
    }

    LaunchedEffect(reserva.fecha, reserva.horaFin) {
        val fechaHoraFin = parsearFechaHoraSeguro(reserva.fecha, "23:59:59") // Fin del día
        if (fechaHoraFin == null) {
            error = true
            tiempoRestante = "--:--"
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

    val isActive = tiempoRestante != "Finalizado" && !error
    val iconRes = when (reserva.tipoReserva) {
        4 -> R.drawable.sala // Sala VIP
        5 -> R.drawable.salon // Salón de reuniones
        else -> R.drawable.icon_restaurante // Restaurante
    }

    val tipoReservaText = when (reserva.tipoReserva) {
        4 -> "Sala VIP"
        5 -> "Salón de Reuniones"
        else -> "Restaurante"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                        text = "00:00 a 23:59", // Hora de inicio y fin para un día completo
                        fontSize = 12.sp,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewReservasenCursoRestauranteScreen() {
    val navController = rememberNavController()
    ReservasenCursoRestauranteScreen(navController = navController)
}