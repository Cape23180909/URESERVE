package edu.ucne.ureserve.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

@Composable
fun DashboardScreen(
    onCategoryClick: (String) -> Unit = {},
    onBottomNavClick: (String) -> Unit = {},
    onProfileIconClick: () -> Unit,
    usuario: UsuarioDTO
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(top = 40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .background(
                    color = Color(0xFF6D87A4),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 26.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Bienvenido, ${usuario.nombres} ${usuario.apellidos}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(52.dp))

                Image(
                    painter = painterResource(id = R.drawable.icon_menu),
                    contentDescription = "Icono de menú",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            onProfileIconClick()
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryCard(
                        title = "Proyectores",
                        iconRes = R.drawable.icon_proyector,
                        onClick = { onCategoryClick("Proyectores") }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    CategoryCard(
                        title = "Restaurante",
                        iconRes = R.drawable.icon_restaurante,
                        onClick = { onCategoryClick("Restaurante") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryCard(
                        title = "Cubículos",
                        iconRes = R.drawable.icon_cubiculo,
                        onClick = { onCategoryClick("Cubículos") }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    CategoryCard(
                        title = "Laboratorios",
                        iconRes = R.drawable.icon_laboratorio,
                        onClick = { onCategoryClick("Laboratorios") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onCategoryClick("Reservaciones") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6D87A4)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_reserva),
                    contentDescription = "Reservaciones",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mis Reservas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(92.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E5C94))
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
                isSelected = true,
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
fun CategoryCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6D87A4)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.W500
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
fun DashboardScreenPreview() {
    MaterialTheme {

        val dummyUsuario = UsuarioDTO(nombres = "Juan", apellidos = "Pérez")

        DashboardScreen(
            onCategoryClick = {},
            onBottomNavClick = {},
            usuario = dummyUsuario,
            onProfileIconClick = {}
        )
    }
}