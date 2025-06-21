package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaRestauranteScreen(
    onBottomNavClick: (String) -> Unit = {},
    onOptionClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        // Encabezado
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
                        painter = painterResource(id = R.drawable.icon_restaurante),
                        contentDescription = "Restaurante",
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6D87A4)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¿Cuál servicio de restaurante desea?",
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de opciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            RestauranteOptionButton(
                title = "Sala VIP",
                iconRes = R.drawable.sala,
                onClick = { onOptionClick("Sala VIP") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            RestauranteOptionButton(
                title = "Salón de reuniones",
                iconRes = R.drawable.salon,
                onClick = { onOptionClick("Salón de reuniones") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            RestauranteOptionButton(
                title = "Restaurante",
                iconRes = R.drawable.comer,
                onClick = { onOptionClick("Restaurante") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navegación inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0238BA))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                iconRes = R.drawable.icon_tutorial,
                label = "Tutorial",
                onClick = { onBottomNavClick("Tutorial") }
            )
            BottomNavItem(
                iconRes = R.drawable.icon_inicio,
                label = "Inicio",
                onClick = { onBottomNavClick("Inicio") }
            )
            BottomNavItem(
                iconRes = R.drawable.icon_perfil,
                label = "Perfil",
                onClick = { onBottomNavClick("Perfil") }
            )
        }
    }
}

@Composable
fun RestauranteOptionButton(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun BottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            colorFilter = if (isSelected) null else androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFFFFDF00) else Color.White,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaRestauranteScreen() {
    MaterialTheme {
        ReservaRestauranteScreen()
    }
}
