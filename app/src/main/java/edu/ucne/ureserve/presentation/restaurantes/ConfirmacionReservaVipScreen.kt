package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmacionReservaVipScreen(
    nombreUsuario: String = "Juan Perez",
    onVolverClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Bienvenido, $nombreUsuario!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023E8A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Términos de reservas de la sala VIP",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        val terminos = listOf(
            "Las reservas las puede hacer cualquier tipo de persona, sin importar que sea estudiante o no.",
            "El costo es de 15,000. Las reservaciones se pueden agendar hasta con un día de antelación e incluso hasta con un año de antelación.",
            "Se pueden hacer siempre y cuando tengan el espacio disponible. Las reservas duran un día completo.",
            "El salón VIP cuenta con proyectores, pantalla de proyección, música, asientos y mesas.",
            "Este apartado cuenta con una capacidad máxima de 15 personas.",
            "Si desea incluir refrigerios, estos serán cargados a la cotización del salón.",
            "Los refrigerios son preparados en la UCNE. También puede contar con meseros, pero aumenta el costo."
        )

        terminos.forEach {
            Text(
                text = "• $it",
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Gracias por utilizar UReserve!",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF023E8A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onVolverClick) {
            Text("Volver al Inicio")
        }
    }
}
