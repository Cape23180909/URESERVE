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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardLaboratorioListScreen(
    selectedDateMillis: Long,
    onLaboratorioSelected: (laboratorioId: Int, laboratorioNombre: String) -> Unit,
    onBackClick: () -> Unit,
    navController: NavController
) {
    val selectedDate = remember {
        Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
        }
    }
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }
    var laboratorioSeleccionado by remember { mutableStateOf<LaboratoriosDto?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF023E8A))) {
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
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fecha seleccionada: ${formatoFecha(selectedDate)}",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
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
                        laboratorio = laboratorio,
                        isSelected = laboratorio == laboratorioSeleccionado,
                        onClick = {
                            laboratorioSeleccionado = laboratorio
                            notificationHandler.showNotification(
                                title = "Laboratorio seleccionado",
                                message = "Has seleccionado: ${laboratorio.nombre}"
                            )
                            navController.navigate("planificador_laboratorio/${laboratorio.laboratorioId}/${laboratorio.nombre}/$selectedDateMillis")
                        }
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
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

fun formatoFecha(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault() // o TimeZone.getTimeZone("UTC")
    return dateFormat.format(calendar.time)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaboratorioCard(
    laboratorio: LaboratoriosDto,
    onClick: () -> Unit,
    isSelected: Boolean,
)
{

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
                text = laboratorio.nombre,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 18.sp  // Tamaño de texto ajustado
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getLaboratorios(): List<LaboratoriosDto> {
    return listOf(
        LaboratoriosDto(1, "Laboratorio A", true),
        LaboratoriosDto(2, "Laboratorio B", true),
        LaboratoriosDto(3, "Laboratorio C", true),
        LaboratoriosDto(4, "Laboratorio D", true),
        LaboratoriosDto(5, "Laboratorio E", true)
    )
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DashboardLaboratorioListScreenPreview() {
//    MaterialTheme {
//        val navController = rememberNavController()
//        DashboardLaboratorioListScreen(
//            selectedDate = Calendar.getInstance(),
//            navController = navController
//        )
//    }
//}