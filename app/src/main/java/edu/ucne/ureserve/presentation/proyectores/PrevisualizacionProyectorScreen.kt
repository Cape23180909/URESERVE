package edu.ucne.ureserve.presentation.proyectores

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrevisualizacionProyectorScreen(
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {},
    fecha: String? = null,
    horaInicio: String? = null,
    horaFin: String? = null,
    navController: NavController
) {
    val horariosDisponibles = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "05:00 PM"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A)) // Fondo azul oscuro
    ) {
        item {
            Column {
                // Encabezado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF6D87A4))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_reserve),
                        contentDescription = "Logo",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = "Confirmación de Reserva",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.icon_proyector),
                        contentDescription = "Proyector",
                        modifier = Modifier.size(50.dp)
                    )
                }

                // Resumen de la reserva
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Resumen de tu Reserva",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Detalles de la reserva
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_reserva),
                            contentDescription = "Fecha",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fecha: ${fecha ?: "No especificada"}",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_clock),
                            contentDescription = "Horario",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Horario: ${horaInicio ?: "--"} - ${horaFin ?: "--"}",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estado de la reserva
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFBDECB6))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Green)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Disponible para reservar",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                // Lista de horarios con la reserva marcada
                Text(
                    text = "Horarios seleccionados:",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {
                    horariosDisponibles.forEach { horario ->
                        val isReserved = horaInicio != null && horaFin != null &&
                                isWithinReservation(horario, horaInicio, horaFin, horariosDisponibles)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    if (isReserved) Color(0xFFBDECB6) else Color.White
                                )
                                .border(width = 1.dp, color = Color.LightGray)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = horario,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                                if (isReserved) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "✔",
                                        color = Color.Green,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Botones inferiores
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp) // Añadido padding inferior
                ) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF004BBB),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "CANCELAR",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            navController.navigate("ReservaExitosa")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6895D2),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "FINALIZAR",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

// Función auxiliar para determinar si un horario está dentro del rango reservado
fun isWithinReservation(horario: String, horaInicio: String, horaFin: String, horarios: List<String>): Boolean {
    val inicioIndex = horarios.indexOf(horaInicio)
    val finIndex = horarios.indexOf(horaFin)
    val currentIndex = horarios.indexOf(horario)

    return currentIndex in inicioIndex..finIndex
}

@Preview(showBackground = true)
@Composable
fun PreviewPrevisualizacionProyectorScreen() {
    MaterialTheme {
        PrevisualizacionProyectorScreen(
            onBack = {},
            onFinish = {},
            fecha = "2023-11-15",
            horaInicio = "10:00 AM",
            horaFin = "12:00 PM",
            navController = rememberNavController()
        )
    }
}