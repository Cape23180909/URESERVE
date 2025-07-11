package edu.ucne.ureserve.presentation.laboratorios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R

@Composable
fun PlanificadorLaboratorioScreen(
    navController: NavController
) {
    val horariosDisponibles = listOf(
        "7:00AM", "7:30AM", "8:00AM", "8:30AM",
        "9:00AM", "9:30AM", "10:00AM", "10:30AM",
        "11:00AM", "11:30AM", "12:00PM", "12:30PM",
        "1:00PM", "2:00PM", "3:00PM",
        "4:00PM", "4:30PM", "5:00PM", "5:30PM",
        "6:00PM", "6:30PM", "7:00PM", "8:00PM", "8:30PM"
    )

    var horaInicioSeleccionada by remember { mutableStateOf("12:00PM") }
    var horaFinSeleccionada by remember { mutableStateOf("02:30PM") }

    var mostrarSeleccionInicio by remember { mutableStateOf(false) }
    var mostrarSeleccionFin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "PLANIFICADORA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "LABORATORIO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(horariosDisponibles) { horario ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = horario,
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccione el horario:",
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Desde:", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { mostrarSeleccionInicio = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = horaInicioSeleccionada, fontSize = 14.sp, color = Color.Black)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Hasta:", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { mostrarSeleccionFin = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = horaFinSeleccionada, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        if (mostrarSeleccionInicio) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                items(horariosDisponibles) { hora ->
                    Text(
                        text = hora,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                horaInicioSeleccionada = hora
                                mostrarSeleccionInicio = false
                                val indexInicio = horariosDisponibles.indexOf(hora)
                                val indexFin = horariosDisponibles.indexOf(horaFinSeleccionada)
                                if (indexFin <= indexInicio) {
                                    horaFinSeleccionada = horariosDisponibles.getOrElse(indexInicio + 1) { horariosDisponibles.last() }
                                }
                            }
                            .padding(8.dp),
                        color = Color.Black
                    )
                }
            }
        }

        if (mostrarSeleccionFin) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                items(
                    horariosDisponibles.filter {
                        horariosDisponibles.indexOf(it) > horariosDisponibles.indexOf(horaInicioSeleccionada)
                    }
                ) { hora ->
                    Text(
                        text = hora,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                horaFinSeleccionada = hora
                                mostrarSeleccionFin = false
                            }
                            .padding(8.dp),
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF004BBB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CANCELAR", fontSize = 14.sp)
            }

            Button(
                onClick = {
                    // Aquí puedes usar horaInicioSeleccionada y horaFinSeleccionada
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6895D2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CONFIRMAR", fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlanificadorLaboratorioScreen() {
    val navController = rememberNavController()
    PlanificadorLaboratorioScreen(navController = navController)
}