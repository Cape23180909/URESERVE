package edu.ucne.ureserve.presentation.buscar_reserva

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.empleados.isReservaFinalizada
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BuscarReservaLaboratorioScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    var codigoReserva by remember { mutableStateOf("") }
    var codigoQR by remember { mutableStateOf("") }
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
                        BasicTextField(
                            value = codigoQR,
                            onValueChange = { codigoQR = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            decorationBox = { innerTextField ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (codigoQR.isEmpty()) {
                                        Text("CODIGO QR", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val currentState = state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is ReservaViewModel.ReservaListState.Success -> {
                    val reservasFiltradas = reservaciones
                        .filterNot { isReservaFinalizada(it.fecha, it.horaFin) }
                        .filter {
                            it.tipoReserva == 3 && (codigoReserva.isBlank() ||
                                    it.codigoReserva.toString().contains(codigoReserva, ignoreCase = true))
                        }

                    if (reservasFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay reservas activas",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
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
                            items(reservasFiltradas) { reserva ->
                                LaboratorioReservationItemBuscar(
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
                }
                is ReservaViewModel.ReservaListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.message,
                            color = Color.Red,
                            fontSize = 18.sp
                        )
                    }
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
fun LaboratorioReservationItemBuscar(
    horaInicio: String,
    horaFin: String,
    fecha: String,
    color: Color,
    onClick: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    fun parsearFechaHoraSeguroLaboratorio(fecha: String, horaInicio: String, horaFin: String): Date? {
        return try {
            val fechaBase = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(fecha)
            if (fechaBase == null) {
                throw IllegalArgumentException("Fecha base no válida")
            }
            val baseFechaFormateada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fechaBase)
            val sdfFinal = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val inicioDate = sdfFinal.parse("$baseFechaFormateada ${horaInicio.take(5)}")
            val finDate = sdfFinal.parse("$baseFechaFormateada ${horaFin.take(5)}")
            if (inicioDate == null || finDate == null) {
                throw IllegalArgumentException("Hora de inicio o fin no válida")
            }
            if (finDate.before(inicioDate)) {
                Date(finDate.time + (24 * 60 * 60 * 1000))
            } else {
                finDate
            }
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewBuscarReservaLaboratorioScreen() {
    val navController = rememberNavController()
    BuscarReservaLaboratorioScreen(navController = navController)
}
