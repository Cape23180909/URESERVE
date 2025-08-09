package edu.ucne.ureserve.presentation.reportes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.reservas.ReservaViewModel

@Composable
fun ReporteProyectoresListScreen(
    navController: NavController
) {
    val viewModel: ReservaViewModel = viewModel()
    viewModel.getReservas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x023E8A)) // Fondo azul oscuro
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x023E8A)) // Fondo azul oscuro
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_table),
                    contentDescription = "Icono de lista",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reporte de Proyectores",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFFFF)) // Fondo blanco
                        .padding(8.dp)
                ) {
                    Text(text = "Fecha", color = Color.Black, modifier = Modifier.weight(1f))
                    Text(text = "Matrícula", color = Color.Black, modifier = Modifier.weight(1f))
                    Text(text = "Nombre del estudiante", color = Color.Black, modifier = Modifier.weight(2f))
                    Text(text = "Horario", color = Color.Black, modifier = Modifier.weight(1f))
                }
            }
            items(viewModel.reservaciones.value) { reservacion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFFFF)) // Fondo blanco
                        .padding(8.dp)
                ) {
                    Text(text = reservacion.fecha, modifier = Modifier.weight(1f))
                    Text(text = reservacion.matricula, modifier = Modifier.weight(1f))
                    Text(text = "Sin nombre", modifier = Modifier.weight(2f)) // Asegúrate de obtener el nombre del estudiante
                    Text(text = "${reservacion.horaInicio}/${reservacion.horaFin}", modifier = Modifier.weight(1f))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Handle volver click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x6D87A4)) // Gris
            ) {
                Text("VOLVER", color = Color.White)
            }
            Button(
                onClick = { /* Handle descargar click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x6EE610)) // Verde
            ) {
                Text("DESCARGAR", color = Color.White)
            }
            Button(
                onClick = { /* Handle imprimir click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD500)) // Naranja
            ) {
                Text("IMPRIMIR", color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReporteProyectoresListScreen() {
    ReporteProyectoresListScreen(
        navController = NavController(LocalContext.current)
    )
}