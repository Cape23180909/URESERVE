package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R

@Composable
fun PagoRestauranteScreen(
    fecha: String,
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp)
    ) {
        // Top Bar
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
                text = "Pago Restaurante",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.comer),
                contentDescription = "Restaurante",
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

        // Métodos de pago
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

                MetodoPagoItem("Efectivo", R.drawable.dinero) {
                    navController.navigate("RegistroReservaRestaurante?fecha=$fecha")
                }
                MetodoPagoItem("Tarjeta de crédito", R.drawable.credito) {
                    navController.navigate("TarjetaCreditoRestaurante?fecha=$fecha")
                }
                MetodoPagoItem("Transferencia bancaria", R.drawable.trasnferencia) {
                    navController.navigate("RestauranteTransferencia?fecha=$fecha")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("RESUMEN DE PEDIDO", fontWeight = FontWeight.Bold, color = Color(0xFF023E8A))
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Reserva Restaurante", color = Color.Black)
                    Text("RD$ 2,500", color = Color.Black)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Fecha:", color = Color.Black)
                    Text(fecha, color = Color.Black)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("TOTAL", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text("RD$ 2,500", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        if (DatosPersonalesRestauranteStore.lista.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "DATOS PERSONALES REGISTRADOS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DatosPersonalesRestauranteStore.lista.forEach { persona ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Nombre: ${persona.nombres}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Dirección: ${persona.direccion}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Capacidad: ${persona.capacidad}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Teléfono: ${persona.telefono}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Correo: ${persona.correo}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Descripción: ${persona.descripcion}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Fecha: ${persona.fecha}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Recorre y guarda cada persona registrada en la API
                DatosPersonalesRestauranteStore.lista.forEach { persona ->
                    val nuevaReserva = DatosPersonalesRestaurante(
                        restauranteId = persona.restauranteId ?: 0,
                        nombres = persona.nombres,
                        direccion = persona.direccion,
                        capacidad = persona.capacidad,
                        telefono = persona.telefono,
                        correo = persona.correo,
                        descripcion = persona.descripcion,
                        fecha = persona.fecha // Ya viene con la fecha asignada si se registró antes
                    )

                    // 2. Crear reservación relacionada
                    viewModel.crearReservacionDesdeRestaurante(
                        fecha = persona.fecha,
                        matricula = persona.correo,
                        horaInicio = persona.horaInicio,
                        horaFin = persona.horaFin
                    )
                }

                // 3. Limpia los datos después del envío
                DatosPersonalesRestauranteStore.lista.clear()

                // 4. Navega a la pantalla de éxito
                val numeroReserva = (1000..9999).random().toString()
                navController.navigate("ReservaRestauranteExitosa?numeroReserva=$numeroReserva")
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
fun MetodoPagoItem(titulo: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = titulo,
            tint = Color(0xFF023E8A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = titulo, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPagoRestauranteScreen() {
    val navController = rememberNavController()
    PagoRestauranteScreen(
        fecha = "2025-06-29",
        navController = navController
    )
}


data class DatosPersonalesRestaurante(
    val restauranteId: Int? = 0,
    val nombres: String = "",
    val apellidos: String = "",
    val cedula: String = "",
    val matricula: String = "",
    val direccion: String = "",
    val capacidad: Int = 0,
    val telefono: String = "",
    val correo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = ""
)

object DatosPersonalesRestauranteStore {
    val lista = mutableStateListOf<DatosPersonalesRestaurante>()
}