package edu.ucne.ureserve.presentation.empleados

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservasenCursoRestauranteScreen(
    navController: NavController,
    viewModel: ReservaViewModel = hiltViewModel()
){
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
                            ReservationRestauramteItem(
                                reserva = reserva,  // Pasar el objeto reserva completo
                                timeRemaining = calcularTiempoRestante(reserva.horaFin),
                                reservationTime = "${reserva.horaInicio} a ${reserva.horaFin}",
                                color = when (reserva.tipoReserva) {
                                    4 -> Color(0xFFFFA500)  // Naranja para SalaVIP
                                    5 -> Color(0xFFADD8E6)  // Azul claro para SalaReuniones
                                    6 -> Color(0xFF6EE610)  // Verde para Restaurante
                                    else -> Color(0xFF6EE610)  // Verde por defecto
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
fun ReservationRestauramteItem(reserva: ReservacionesDto, timeRemaining: String, reservationTime: String, color: Color) {
    val iconRes = when (reserva.tipoReserva) {
        4 -> R.drawable.sala  // Icono para SalaVIP
        5 -> R.drawable.salon  // Icono para SalaReuniones
        6 -> R.drawable.comer  // Icono para Restaurante
        else -> R.drawable.icon_restaurante  // Icono por defecto
    }

    val tipoReservaText = when (reserva.tipoReserva) {
        4 -> "Sala VIP"
        5 -> "Salón de Reuniones"
        6 -> "Restaurante"
        else -> "Reservación"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White)
                .widthIn(min = 100.dp)
                .padding(8.dp)
        ) {
            Text(
                text = timeRemaining,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(color)
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
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ahora",
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reservationTime,
                        fontSize = 12.sp,
                        color = Color.Black,
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