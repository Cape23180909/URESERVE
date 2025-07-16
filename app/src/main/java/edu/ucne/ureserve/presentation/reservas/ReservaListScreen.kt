package edu.ucne.ureserve.presentation.reservas

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaListScreen(
    onBottomNavClick: (String) -> Unit,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
                                contentDescription = "Cubiculo",
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reservas en Curso",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            when (state) {
                is ReservaViewModel.ReservaListState.Loading -> {
                    // Muestra un indicador de carga
                    Text("Cargando reservas...", color = Color.White)
                }
                is ReservaViewModel.ReservaListState.Success -> {
                    val reservas = (state as ReservaViewModel.ReservaListState.Success).reservas
                    if (reservas.isEmpty()) {
                        Text(
                            text = "No tienes reservas activas",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        ReservaList(reservas = reservas)
                    }
                }
                is ReservaViewModel.ReservaListState.Error -> {
                    val message = (state as ReservaViewModel.ReservaListState.Error).message
                    Text("Error: $message", color = Color.Red)
                }

                is ReservaViewModel.ReservaListState.Error -> TODO()
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
            .padding(horizontal = 16.dp)
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6D87A4)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Tipo: ${getTipoReservaString(reserva.tipoReserva)}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Fecha: ${reserva.fecha}",
                color = Color.White
            )
            Text(
                text = "Horario: ${reserva.horario}",
                color = Color.White
            )
            Text(
                text = "Estado: ${getEstadoString(reserva.estado)}",
                color = Color.White
            )
            if (reserva.tipoReserva == 1) { // Solo mostrar cantidad para restaurante
                Text(
                    text = "Personas: ${reserva.cantidadEstudiantes}",
                    color = Color.White
                )
            }
        }
    }
}

fun getTipoReservaString(tipo: Int): String {
    return when (tipo) {
        1 -> "Proyector"
        2 -> "Cubiculo"
        3 -> "Laboratorio"
        4 -> "Restaurante"
        else -> "Desconocido"
    }
}

fun getEstadoString(estado: Int): String {
    return when (estado) {
        1 -> "Disponible"
        2 -> "En uso"
        3 -> "Finalizada"
        4 -> "Cancelada"
        else -> "Desconocido"
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaListScreen() {
    MaterialTheme {
        ReservaListScreen(onBottomNavClick = {})
    }
}