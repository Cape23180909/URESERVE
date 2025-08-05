package edu.ucne.ureserve.presentation.reservas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import edu.ucne.ureserve.R
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesReservacionScreen(
    reservaId: Int,
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

    // Construimos la data para el QR en formato JSON para mejor legibilidad
    val qrDataRaw = """
        {
            "fecha": "$fecha",
            "hora_inicio": "$horaInicio",
            "hora_fin": "$horaFin",
            "matricula": "$matricula",
            "tipo_reserva": "$tipoReserva"
        }
    """.trimIndent()

    // Codificamos la data para URL
    val qrDataEncoded = URLEncoder.encode(qrDataRaw, StandardCharsets.UTF_8.toString())
    val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=$qrDataEncoded"

    val showDialog = remember { mutableStateOf(false) }

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
                                    modifier = Modifier.size(60.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.icon_reserva),
                                    contentDescription = "Reserva",
                                    modifier = Modifier.size(60.dp))
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Atrás",
                                    tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF6D87A4))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF023E8A)))
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
                    modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = nombreTipo,
                    color = Color.White,
                    fontSize = 22.sp)
            }

            InfoRow("FECHA:", fecha)
            InfoRow("HORA:", "$horaInicio - $horaFin")
            InfoRow("MATRÍCULA:", matricula)
            InfoRow("TIPO:", tipoReserva)

            Spacer(modifier = Modifier.height(24.dp))

            // Sección del QR con mejor presentación
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Código QR de la Reserva",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp))

                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    AsyncImage(
                        model = qrUrl,
                        contentDescription = "Código QR de la reserva",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp))
                }

                Text(
                    text = "Escanea este código para verificar la reserva",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48CAE4)),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Ampliar QR", color = Color.Black)
                }
            }

            // Diálogo para mostrar el QR ampliado
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("CERRAR", color = Color(0xFF023E8A))
                        }
                    },
                    title = {
                        Text("Código QR de Reserva", color = Color.Black)
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = qrUrl,
                                contentDescription = "Código QR Ampliado",
                                modifier = Modifier
                                    .size(300.dp)
                                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Información contenida:",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Fecha: $fecha\nHora: $horaInicio - $horaFin\nMatrícula: $matricula",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    },
                    containerColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Sección de botones de acción
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    modifier = Modifier.fillMaxWidth()
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
                            when (tipoReserva.uppercase()) {
                                "CUBÍCULO" -> navController?.navigate("modificar_cubiculo/$reservaId")
                                "PROYECTOR" -> navController?.navigate("modificar_proyector/$reservaId")
                                "LABORATORIO" -> navController?.navigate("modificar_laboratorio/$reservaId")
                                "RESTAURANTE" -> navController?.navigate("modificar_restaurante/$reservaId")
                                "SALON" -> navController?.navigate("modificar_salon/$reservaId")
                                "SALA" -> navController?.navigate("modificar_sala_vip/$reservaId")
                            }
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
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp)
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f, fill = false))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDetallesReservacionScreen() {
    DetallesReservacionScreen(
        reservaId = 1, // ejemplo
        fecha = "2025-08-01",
        horaInicio = "10:00",
        horaFin = "12:00",
        matricula = "A12345",
        tipoReserva = "PROYECTOR",
        navController = null
    )
}
