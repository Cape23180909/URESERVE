package edu.ucne.ureserve.presentation.proyectores

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaExitosaScreen(
    navController: NavController,
    codigoReserva: String,  // Añadir parámetro para el código de reserva
    onBottomNavClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 350.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_check),
                        contentDescription = "Reserva Exitosa",
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Green, shape = RoundedCornerShape(100))
                            .padding(8.dp),
                        tint = Color.White
                    )

                    Text(
                        text = "Reserva exitosa!",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "No. de Reserva: $codigoReserva",  // Usar el código real
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            navController.navigate("Dashboard") {
                                popUpTo(0)  // Limpiar el back stack
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6895D2),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .width(150.dp)
                    ) {
                        Text(
                            text = "CONTINUAR",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Barra inferior de navegación (sin cambios)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF023E8A))
                .padding(vertical = 12.dp)
        ) {
            BottomNavItem(
                iconRes = R.drawable.icon_proyector,
                label = "Proyectores",
                isSelected = false,
                onClick = { onBottomNavClick("Proyectores") }
            )
            BottomNavItem(
                iconRes = R.drawable.icon_inicio,
                label = "Inicio",
                isSelected = true,
                onClick = { onBottomNavClick("Inicio") }
            )
            BottomNavItem(
                iconRes = R.drawable.icon_perfil,
                label = "Perfil",
                isSelected = false,
                onClick = { onBottomNavClick("Perfil") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaExitosaScreen() {
    MaterialTheme {
        ReservaExitosaScreen(
            navController = rememberNavController(),
            codigoReserva = "ABC123" // Valor de ejemplo
        )
    }
}