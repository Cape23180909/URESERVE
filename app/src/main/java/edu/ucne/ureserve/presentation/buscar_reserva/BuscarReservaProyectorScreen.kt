package edu.ucne.ureserve.presentation.buscar_reserva

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.empleados.isReservaFinalizada
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun BuscarReservaProyectorScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    var codigoReserva by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getReservas()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F3278)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
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

            Text(
                text = "Reservas de proyectores en Curso",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E5C94))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "BUSCAR RESERVA",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = codigoReserva,
                            onValueChange = { codigoReserva = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            decorationBox = { innerTextField ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (codigoReserva.isEmpty()) {
                                        Text("CODIGO RESERVA", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            when (state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                is ReservaViewModel.ReservaListState.Success -> {
                    val reservasFiltradas = reservaciones
                        .filterNot { isReservaFinalizada(it.fecha, it.horaFin) }
                        .filter {
                            codigoReserva.isBlank() ||
                                    it.codigoReserva.toString().contains(codigoReserva, ignoreCase = true)
                        }

                    if (reservasFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay reservas activas",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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

                            reservasFiltradas.forEach { reserva ->
                                ProyectorReservationItemBuscar(
                                    horaInicio = reserva.horaInicio,
                                    horaFin = reserva.horaFin,
                                    fecha = reserva.fecha,
                                    color = Color(0xFF6EE610),
                                    onClick = {
                                        navController.navigate(
                                            "detalleReservaProyector/${reserva.codigoReserva}/${reserva.fecha}/${reserva.horaInicio}/${reserva.horaFin}/${reserva.matricula}"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is ReservaViewModel.ReservaListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (state as ReservaViewModel.ReservaListState.Error).message,
                            fontSize = 18.sp,
                            color = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
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
}

@Composable
fun ProyectorReservationItemBuscar(
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
            .clickable { onClick() }
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
                    painter = painterResource(id = R.drawable.icon_proyector),
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

fun parsearFechaHoraSeguro(fecha: String, hora: String): Date? {
    return try {
        val fechaLimpia = fecha.take(10)
        val horaLimpia = hora.take(5)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewBuscarReservaProyectorScreen() {
    val navController = rememberNavController()
    BuscarReservaProyectorScreen(navController = navController)
}