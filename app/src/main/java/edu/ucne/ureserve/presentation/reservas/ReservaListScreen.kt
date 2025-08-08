package edu.ucne.ureserve.presentation.reservas

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReservaListScreen(
    navController: NavHostController? = null,
    onBottomNavClick: (String) -> Unit,
    viewModel: ReservaViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else null

    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getReservasUsuario()
    }

    val shouldRefresh by navController
        ?.previousBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Boolean?>("shouldRefresh", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh == true) {
            viewModel.getReservasUsuario()
            navController?.previousBackStackEntry
                ?.savedStateHandle
                ?.set("shouldRefresh", null)
        }
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
                            text = "Reservas Planificadas:",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        ReservaList(
                            reservas = reservas,
                            onReservaClick = { reserva ->
                                val (nombreTipo, _) = getIconForTipo(reserva.tipoReserva)

                                notificationHandler.showNotification(
                                    title = "Reserva Seleccionada",
                                    message = "Has seleccionado una reserva de tipo $nombreTipo el ${reserva.fechaFormateada}."
                                )
                                navController?.navigate(
                                    "detallesReserva/${reserva.reservacionId}/${reserva.fechaFormateada}/${reserva.horaInicio}/${reserva.horaFin}/${reserva.matricula}/${nombreTipo}"
                                )
                            }
                        )

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
fun ReservaList(
    reservas: List<ReservacionesDto>,
    onReservaClick: (ReservacionesDto) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(reservas) { reserva ->
            ReservaCard(reserva = reserva, onClick = { onReservaClick(reserva) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun ReservaCard(reserva: ReservacionesDto, onClick: () -> Unit) {
    val (nombreTipo, iconoTipo) = getIconForTipo(reserva.tipoReserva)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconoTipo),
                contentDescription = nombreTipo,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = nombreTipo,
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

fun getIconForTipo(tipo: Int): Pair<String, Int> {
    return when (tipo) {
        1 -> Pair("PROYECTOR", R.drawable.icon_proyector)
        2 -> Pair("CUBÍCULO", R.drawable.icon_cubiculo)
        3 -> Pair("LABORATORIO", R.drawable.icon_laboratorio)
        4 -> Pair("SALA", R.drawable.sala)
        5 -> Pair("SALÓN", R.drawable.salon)
        6 -> Pair("RESTAURANTE", R.drawable.icon_restaurante)
        else -> Pair("RESERVA", R.drawable.icon_reserva)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaListScreen() {
    MaterialTheme {
        ReservaListScreen(onBottomNavClick = {})
    }
}