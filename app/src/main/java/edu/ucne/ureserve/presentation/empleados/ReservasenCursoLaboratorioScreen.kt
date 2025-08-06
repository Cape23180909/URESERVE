package edu.ucne.ureserve.presentation.empleados

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 👇 pantalla completa fusionada con temporizador

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservasenCursoLaboratorioScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        viewModel.getLaboratorioReservas()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getLaboratorioReservas()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F3278))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(Color(0xFFA7A7A7)).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(painterResource(id = R.drawable.logo_reserve), null, Modifier.size(50.dp))
                Image(painterResource(id = R.drawable.icon_reserva), null, Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Reservas de laboratorios en Curso",
                fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val currentState = state) {
                is ReservaViewModel.ReservaListState.Loading ->
                    Text("Cargando reservas...", color = Color.White, fontSize = 18.sp)

                is ReservaViewModel.ReservaListState.Success -> {
                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2E5C94))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tiempo Restante", color = Color.White)
                                Text("Reservas", color = Color.White)
                            }
                        }
                        items(reservaciones.filter { it.tipoReserva == 3 }) { reserva ->
                            LaboratorioReservationItem(
                                horaInicio = reserva.horaInicio,
                                horaFin = reserva.horaFin,
                                fecha = reserva.fecha,
                                color = Color(0xFF6EE610),
                                onClick = {
                                    navController.navigate(
                                        "detalleReservaLaboratorio/${reserva.codigoReserva}/${reserva.fecha}/${reserva.horaInicio}/${reserva.horaFin}/${reserva.matricula}"
                                    )
                                }
                            )
                        }
                    }
                }

                is ReservaViewModel.ReservaListState.Error ->
                    Text(
                        currentState.message,
                        color = Color.Red, fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .width(150.dp).height(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
            ) {
                Text("Volver", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun LaboratorioReservationItem(
    horaInicio: String,
    horaFin: String,
    fecha: String,
    color: Color,
    onClick: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(fecha, horaFin) {
        val fechaHoraFin = parsearFechaHoraSeguroLaboratorio(fecha, horaInicio, horaFin)
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
                    painter = painterResource(id = R.drawable.icon_laboratorio),
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

private val sdfLaboratorio = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
private fun parsearFechaHoraSeguroLaboratorio(fecha: String, horaInicio: String, horaFin: String): Date? {
    return try {
        // Parsear la fecha base
        val fechaBase = sdfLaboratorio.parse(fecha)
        if (fechaBase == null) {
            throw IllegalArgumentException("Fecha base no válida")
        }

        // Formatear la fecha base sin hora
        val baseFechaFormateada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fechaBase)

        // Crear SimpleDateFormat para combinar fecha y hora
        val sdfFinal = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Parsear hora de inicio y fin
        val inicioDate = sdfFinal.parse("$baseFechaFormateada ${horaInicio.take(5)}")
        val finDate = sdfFinal.parse("$baseFechaFormateada ${horaFin.take(5)}")

        if (inicioDate == null || finDate == null) {
            throw IllegalArgumentException("Hora de inicio o fin no válida")
        }

        // Manejar el cruce de días
        if (finDate.before(inicioDate)) {
            Date(finDate.time + (24 * 60 * 60 * 1000)) // Sumar un día al tiempo de fin
        } else {
            finDate
        }
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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewReservasenCursoLaboratorioScreen() {
    val navController = rememberNavController()
    // Para el preview, puedes simular un ViewModel o pasar una lista vacía/mockeada si es necesario.
    // Por simplicidad, se mantiene así, pero podría fallar si el ViewModel hace llamadas de red en init.
    ReservasenCursoLaboratorioScreen(navController = navController)
}