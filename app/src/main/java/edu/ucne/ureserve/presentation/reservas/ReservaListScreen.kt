package edu.ucne.ureserve.presentation.reservas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaListScreen(
    onBottomNavClick: (String) -> Unit,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getReservasUsuario()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_reserve),
                                contentDescription = "Logo",
                                modifier = Modifier.size(60.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Reserva",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6D87A4)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF023E8A))
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E5C94))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    iconRes = R.drawable.icon_inicio,
                    label = "Inicio",
                    isSelected = true,
                    onClick = { onBottomNavClick("Inicio") }
                )
            }
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    Text("Cargando reservas...", color = Color.White)
                }

                is ReservaViewModel.ReservaListState.Success -> {
                    val reservas = (state as ReservaViewModel.ReservaListState.Success).reservas
                    if (reservas.isNotEmpty()) {
                        Text(
                            text = "Reservas",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        ReservaList(reservas)
                    } else {
                        Text(
                            text = "No tienes reservas activas",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is ReservaViewModel.ReservaListState.Error -> {
                    val message = (state as ReservaViewModel.ReservaListState.Error).message
                    Text("Error: $message", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ReservaList(reservas: List<ReservacionesDto>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(reservas) { reserva ->
            ReservaCard(reserva = reserva)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ReservaCard(reserva: ReservacionesDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getIconForTipo(reserva.tipoReserva)),
                contentDescription = "Icono",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Reservación",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = reserva.fechaFormateada,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "${reserva.horaInicio} A ${reserva.horaFin}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getIconForTipo(tipo: Int): Int {
    return when (tipo) {
        1 -> R.drawable.icon_proyector
        2 -> R.drawable.icon_cubiculo
        3 -> R.drawable.icon_laboratorio
        4 -> R.drawable.sala
        5 -> R.drawable.salon
        6 -> R.drawable.icon_restaurante
        else -> R.drawable.icon_reserva
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaListScreen() {
    MaterialTheme {
        ReservaListScreen(onBottomNavClick = {})
    }
}