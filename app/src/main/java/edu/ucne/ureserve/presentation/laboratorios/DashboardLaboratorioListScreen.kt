package edu.ucne.ureserve.presentation.laboratorios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardLaboratorioListScreen(
    selectedDate: Calendar?,
    onLaboratorioSelected: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    navController: NavController
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF023E8A))) {

        // TopAppBar (se mantiene igual)
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
                        modifier = Modifier.size(60.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.icon_laboratorio),
                        contentDescription = "Icono de Laboratorio",
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6D87A4)
            )
        )

        // Contenido principal con weight
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elige el laboratorio deseado:",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(getLaboratorios()) { laboratorio ->
                    LaboratorioCard(
                        nombre = laboratorio,
                        onClick = {
                            navController.navigate("planificador_laboratorio")
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp) // Margen inferior adicional
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E5C94))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    iconRes = R.drawable.icon_inicio,
                    label = "Inicio",
                    isSelected = true,
                    onClick = { /* onBottomNavClick("Inicio") */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaboratorioCard(nombre: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 54.dp, vertical = 1.dp),  // Ajuste de padding para separación
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6D87A4)
        ),
        shape = MaterialTheme.shapes.medium  // Bordes redondeados uniformes
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono centrado arriba
            Image(
                painter = painterResource(id = R.drawable.icon_laboratorio),
                contentDescription = "Icono Laboratorio",
                modifier = Modifier
                    .size(48.dp)  // Tamaño aumentado para mejor visibilidad
                    .padding(bottom = 8.dp)
            )

            // Nombre del laboratorio centrado
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 18.sp  // Tamaño de texto ajustado
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getLaboratorios(): List<String> {
    return listOf(
        "Laboratorio A",
        "Laboratorio B",
        "Laboratorio C",
        "Laboratorio D",
        "Laboratorio E"
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardLaboratorioListScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        DashboardLaboratorioListScreen(
            selectedDate = Calendar.getInstance(),
            navController = navController
        )
    }
}