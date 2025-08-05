package edu.ucne.ureserve.presentation.empleados

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat // Importado para parsearFechaHoraSeguro
import java.util.Date             // Importado para el tipo de retorno de parsearFechaHoraSeguro
import java.util.Locale           // Importado para SimpleDateFormat

// Funciones auxiliares movidas al nivel superior del archivo o a un archivo de utilidades
// para que sean accesibles por LaboratorioReservationItem

// Asegúrate de que esta función esté definida aquí o importada correctamente.
private fun parsearFechaHoraSeguro(fecha: String, hora: String): Date? {
    return try {
        val fechaLimpia = fecha.take(10) // Asume formato yyyy-MM-dd
        val horaLimpia = hora.take(5)    // Asume formato HH:mm
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.parse("$fechaLimpia $horaLimpia")
    } catch (e: Exception) {
        // e.printStackTrace() // Considera loguear el error para depuración
        null
    }
}

private fun formatearTiempoRestante(diferencia: Long): String {
    val segundosTotales = diferencia / 1000
    val horas = segundosTotales / 3600
    val minutos = (segundosTotales % 3600) / 60
    val segundosRestantes = segundosTotales % 60 // Corregido el nombre de la variable

    // Formato HH:MM:SS o el que prefieras
    return String.format("%02dh %02dmin %02ds", horas, minutos, segundosRestantes)
}

//fun isReservaFinalizada(fecha: String, horaFin: String): Boolean {
//    val fechaHoraFin = parsearFechaHoraSeguro(fecha, horaFin)
//    return fechaHoraFin?.let { it.time < System.currentTimeMillis() } ?: false
//}

@RequiresApi(Build.VERSION_CODES.O) // Mantenla si usas otras APIs de nivel O, aunque el temporizador ahora no lo requiere estrictamente.
@Composable
fun ReservasenCursoLaboratorioScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()

    LaunchedEffect(Unit) {
        // Asegúrate de que este nombre de función sea correcto en tu ViewModel
        viewModel.getLaboratorioReservas() // o getLagboratorioReservas() como tenías antes, verifica el nombre
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
                text = "Reservas de laboratorios en Curso",
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
                    if (reservaciones.isEmpty()) {
                        Text(
                            text = "No hay reservas de laboratorios en curso.",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
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
                            // Filtra las reservaciones finalizadas directamente aquí
                            val reservacionesActivas = reservaciones.filterNot { isReservaFinalizada(it.fecha, it.horaFin) }

                            if (reservacionesActivas.isEmpty() && reservaciones.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Todas las reservas de laboratorio han finalizado.",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                                    )
                                }
                            } else {
                                items(reservacionesActivas) { reserva ->
                                    LaboratorioReservationItem(
                                        fecha = reserva.fecha,
                                        horaInicio = reserva.horaInicio,
                                        horaFin = reserva.horaFin,
                                        color = Color(0xFF6EE610)
                                        // onTimerFinished = {
                                        //    viewModel.refreshReservas() // Ejemplo de acción al finalizar
                                        // }
                                    )
                                }
                            }
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

@Composable
fun LaboratorioReservationItem(
    horaInicio: String,
    horaFin: String,
    fecha: String, // Asegúrate de que este parámetro esté aquí
    color: Color,
    onTimerFinished: () -> Unit = {}
) {
    var tiempoRestante by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    // Este es el LaunchedEffect que proporcionaste, ahora debería funcionar
    // si parsearFechaHoraSeguro y formatearTiempoRestante están definidas correctamente.
    LaunchedEffect(fecha, horaFin) {
        val fechaHoraFin = parsearFechaHoraSeguro(fecha, horaFin)
        if (fechaHoraFin == null) {
            error = true
            tiempoRestante = "--:--:--" // Mostrar algo en caso de error de parseo
            return@LaunchedEffect
        }
        while (true) {
            val ahora = System.currentTimeMillis()
            // Accedemos a .time de java.util.Date
            val diff = fechaHoraFin.time - ahora
            if (diff > 0) {
                tiempoRestante = formatearTiempoRestante(diff)
            } else {
                tiempoRestante = "Finalizado"
                onTimerFinished()
                break
            }
            // Pequeña optimización para el delay
            delay(1000L - (ahora % 1000L))
        }
    }

    val tiempoMostrado = tiempoRestante ?: if (error) "--:--:--" else "Cargando..."
    val isActive = tiempoMostrado != "Finalizado" && !error
    val colorFondo = if (isActive) color else Color.Gray
    val textColor = if (isActive) Color.Black else Color.DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                color = textColor
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
                    painter = painterResource(id = R.drawable.icon_laboratorio), // Asegúrate de tener este drawable
                    contentDescription = "Reserva Laboratorio",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Reservación", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text("Laboratorio", fontSize = 12.sp, color = textColor) // "Ahora" o "Laboratorio"
                    Text(
                        text = "$horaInicio a $horaFin",
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
            }
        }
    }
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