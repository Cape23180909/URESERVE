package edu.ucne.ureserve.presentation.salones

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.restaurantes.MetodoPagoSalaVipItem

@Composable
fun PagoSalonScreen(
    fecha: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Pago Salón de Reuniones",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.salon), // Debes tener este recurso
                contentDescription = "Salón",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Fecha seleccionada: $fecha",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECCIONE EL MÉTODO DE PAGO",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                MetodoPagoSalonItem("Efectivo", R.drawable.dinero) {
                    navController.navigate("RegistroReservaSalon?fecha=$fecha")
                }

                MetodoPagoSalonItem("Tarjeta de crédito", R.drawable.credito) {
                    navController.navigate("TarjetaCreditoSalon?fecha=$fecha")
                }

                MetodoPagoSalonItem("Transferencia bancaria", R.drawable.trasnferencia) {
                    navController.navigate("SalonTransferencia?fecha=$fecha")
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RESUMEN DE PEDIDO",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reserva Salón de Reuniones", color = Color.Black)
                    Text("RD$ 15,000", color = Color.Black)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fecha:", color = Color.Black)
                    Text(fecha, color = Color.Black)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("RD$ 15,000", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        if (DatosPersonalesSalonStore.lista.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DATOS PERSONALES REGISTRADOS",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DatosPersonalesSalonStore.lista.forEach { persona ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${persona.nombres} ${persona.apellidos}", color = Color(0xFF023E8A), fontSize = 14.sp)
                        Text("Correo: ${persona.correo}", fontSize = 14.sp, color = Color.DarkGray)
                        Text("Celular: ${persona.celular}", fontSize = 14.sp, color = Color.DarkGray)
                        Text("Matrícula: ${persona.matricula}", fontSize = 14.sp, color = Color.DarkGray)
                        Text("Cédula: ${persona.cedula}", fontSize = 14.sp, color = Color.DarkGray)
                        Text("Dirección: ${persona.direccion}", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val numeroReserva = (1000..9999).random().toString()
                DatosPersonalesSalonStore.lista.clear()
                navController.navigate("ReservaExitosaSalon?numeroReserva=$numeroReserva")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0077B6),
                contentColor = Color.White
            )
        ) {
            Text("COMPLETAR RESERVA", fontWeight = FontWeight.Bold)
        }

    }
}

@Composable
fun MetodoPagoSalonItem(titulo: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = titulo,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = titulo, fontSize = 16.sp, color = Color.Black)
    }
}

// Modelo de datos personales para salón
data class DatosPersonalesSalon(
    val correo: String,
    val nombres: String,
    val apellidos: String,
    val celular: String,
    val matricula: String,
    val cedula: String,
    val direccion: String
)

object DatosPersonalesSalonStore {
    val lista = mutableStateListOf<DatosPersonalesSalon>()
}

@Preview(showBackground = true)
@Composable
fun PreviewPagoSalonScreen() {
    val navController = rememberNavController()
    PagoSalonScreen(fecha = "20/06/2025", navController = navController)
}
