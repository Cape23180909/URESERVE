package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.ucne.ureserve.R

@Composable
fun ReservaRestauranteExitosaScreen(
    numeroReserva: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.existo), // Asegúrate de tener esta imagen en res/drawable
            contentDescription = "Reserva exitosa",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Reserva exitosa!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No. de Reserva: $numeroReserva",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("Dashboard") {
                    popUpTo(0) // Limpia el backstack
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            )
        ) {
            Text("CONTINUAR", fontWeight = FontWeight.Bold)
        }
    }
}
