package edu.ucne.ureserve.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R

@Composable
fun DashboardAdminReportScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFA7A7A7))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.icon_reserva),
                contentDescription = "Reservas en Curso",
                modifier = Modifier.size(50.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleccione el reporte deseado",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReportButton(
                        icon = painterResource(id = R.drawable.icon_proyector),
                        text = "Reportes de Proyectores",
                        modifier = Modifier.weight(1f)
                    )
                    ReportButton(
                        icon = painterResource(id = R.drawable.cubiculo_admin),
                        text = "Reportes de Cubículos",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReportButton(
                        icon = painterResource(id = R.drawable.icon_laboratorio),
                        text = "Reporte de Laboratorios",
                        modifier = Modifier.weight(1f)
                    )
                    ReportButton(
                        icon = painterResource(id = R.drawable.icon_restaurante),
                        text = "Reportes de Restaurante",
                        modifier = Modifier.weight(1f)
                    )
                }

                ReportButton(
                    icon = painterResource(id = R.drawable.icon_table),
                    text = "REPORTES GENERALES",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94))
            ) {
                Text(
                    text = "Volver",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun ReportButton(icon: Painter, text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        modifier = modifier
            .height(120.dp)
            .background(Color(0xFFFFD600), shape = RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardAdminReportScreen() {
    val navController = rememberNavController()
    DashboardAdminReportScreen(navController = navController)
}