package edu.ucne.ureserve.presentation.reservas

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaListScreen(
    onBottomNavClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
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
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Cubiculo",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6D87A4)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF023E8A))
                )
            }
        },
        bottomBar = {
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
                    onClick = { onBottomNavClick("Inicio") }
                )
            }
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reservas en Curso",
                color = Color.White, // Cambié el color del texto a blanco para mejor contraste
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaListScreen() {
    MaterialTheme {
        ReservaListScreen(onBottomNavClick = {})
    }
}