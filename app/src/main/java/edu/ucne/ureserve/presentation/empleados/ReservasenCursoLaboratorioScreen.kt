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
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservasenCursoLaboratorioScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val reservaciones by viewModel.reservaciones.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getLagboratorioReservas()
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

                        items(reservaciones) { reserva ->
                            LaboratorioReservationItem(
                                timeRemaining = calcularTiempoRestante(reserva.horaFin),
                                reservationTime = "${reserva.horaInicio} a ${reserva.horaFin}",
                                color = Color(0xFF6EE610)
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

@Composable
fun LaboratorioReservationItem(timeRemaining: String, reservationTime: String, color: Color) {
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
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(timeRemaining, color = Color.Black, fontSize = 16.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(color)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_laboratorio),
                    contentDescription = "Reserva",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Reservación", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Ahora", fontSize = 12.sp)
                    Text(reservationTime, fontSize = 12.sp)
                }
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//fun calcularTiempoRestante(horaFin: String): String {
//    return try {
//        val formato = DateTimeFormatter.ofPattern("HH:mm")
//        val horaFinal = LocalTime.parse(horaFin, formato)
//        val ahora = LocalTime.now()
//
//        if (ahora.isBefore(horaFinal)) {
//            val diferencia = Duration.between(ahora, horaFinal)
//            "${diferencia.toMinutes()} min"
//        } else {
//            "Finalizado"
//        }
//    } catch (e: Exception) {
//        "00:00 min"
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewReservasenCursoLaboratorioScreen() {
    val navController = rememberNavController()
    ReservasenCursoLaboratorioScreen(navController = navController)
}