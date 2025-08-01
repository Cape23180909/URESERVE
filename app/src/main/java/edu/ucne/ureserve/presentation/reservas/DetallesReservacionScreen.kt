package edu.ucne.ureserve.presentation.reservas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesReservacionScreen(
    fecha: String,
    horaInicio: String,
    horaFin: String,
    matricula: String,
    tipoReserva: String,
    onCancelarReserva: (() -> Unit)? = null,
    navController: NavHostController? = null
) {
    val (nombreTipo, iconoTipo) = when (tipoReserva.uppercase()) {
        "PROYECTOR" -> Pair("PROYECTOR", R.drawable.icon_proyector)
        "CUBÍCULO" -> Pair("CUBÍCULO", R.drawable.icon_cubiculo)
        "LABORATORIO" -> Pair("LABORATORIO", R.drawable.icon_laboratorio)
        "SALA" -> Pair("SALA", R.drawable.sala)
        "SALÓN" -> Pair("SALÓN", R.drawable.salon)
        "RESTAURANTE" -> Pair("RESTAURANTE", R.drawable.icon_restaurante)
        else -> Pair("RESERVA", R.drawable.icon_reserva)
    }

    val qrData = "Fecha:$fecha\nHora:$horaInicio a $horaFin\nMatricula:$matricula"
    val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=$qrData"

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
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF023E8A))
                )
            }
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = iconoTipo),
                    contentDescription = nombreTipo,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = nombreTipo,
                    color = Color.White,
                    fontSize = 22.sp
                )
            }

            InfoRow("FECHA:", fecha)
            InfoRow("HORA:", "$horaInicio A $horaFin")
            InfoRow("MATRÍCULA:", matricula)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("QR:", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))

                AsyncImage(
                    model = qrUrl,
                    contentDescription = "Código QR",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* TODO: Finalizar reserva */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("FINALIZAR RESERVA", color = Color.White)
                }

                Button(
                    onClick = {
                        onCancelarReserva?.invoke()
                        navController?.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CANCELAR", color = Color.White)
                }

            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Button(
                    onClick = { navController?.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("REGRESAR", color = Color.White)
                }


                Button(
                    onClick = {
                        navController?.navigate("modificar_proyector")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48CAE4)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("MODIFICAR", color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White, fontSize = 16.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp)
    }
}
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewDetallesReservacionScreen() {
    DetallesReservacionScreen(
        fecha = "2025-08-01",
        horaInicio = "10:00",
        horaFin = "12:00",
        matricula = "A12345",
        tipoReserva = "PROYECTOR",
        navController = null
    )
}
