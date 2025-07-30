package edu.ucne.ureserve.presentation.cubiculos

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DashboardCubiculoScreen(
    fecha: String = "Hoy",
    onBottomNavClick: (String) -> Unit = {},
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    usuarioDTO: UsuarioDTO,
    navController: NavController
) {
    val context = LocalContext.current

    // Notificación
    val notificationHandler = remember { NotificationHandler(context) }

    // Permiso para Android 13+
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
        } else null

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    val cubiculos by viewModel.cubiculos.collectAsState()

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
                        painter = painterResource(id = R.drawable.icon_cubiculo),
                        contentDescription = "Icono de Cubículo",
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
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "Reserve Ahora!!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(25.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 50.dp,
                bottom = 70.dp
            ),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            items(cubiculos.size) { index ->
                val cubiculo = cubiculos[index]
                CubiculoCard(
                    cubiculo = cubiculo,
                    onClick = {
                        if (cubiculo.disponible) {
                            // Notificación
                            notificationHandler.showNotification(
                                title = "Cubículo Seleccionado",
                                message = "Seleccionaste el ${cubiculo.nombre}"
                            )
                            val jsonUsuario = Json.encodeToString(usuarioDTO)
                            navController.navigate("reserva/${cubiculo.cubiculoId}?usuario=${URLEncoder.encode(jsonUsuario, "UTF-8")}")
                        }
                    }
                )
            }
        }
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

@Composable
fun CubiculoCard(
    cubiculo: CubiculosDto,
    onClick: () -> Unit
) {
    val borderColor = if (cubiculo.disponible) Color.Green else Color.Red

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cubiculo.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (cubiculo.disponible) {
                    Color(0xFF03045E) // Azul oscuro si disponible
                } else {
                    Color(0xFF6C757D) // Gris si no disponible
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
            )

            if (!cubiculo.disponible) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "No disponible",
                        tint = Color(0xFFDC2F02) // Rojo/naranja
                    )
                    Text(
                        text = "NO DISPONIBLE",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2F02),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservaCubiculoScreenPreview() {
    val cubiculosEjemplo = listOf(
        CubiculosDto(
            cubiculoId = 1,
            nombre = "Cubículo #1",
            disponible = true
        ),
        CubiculosDto(
            cubiculoId = 2,
            nombre = "Cubículo #2",
            disponible = false
        ),
        CubiculosDto(
            cubiculoId = 3,
            nombre = "Cubículo #3",
            disponible = true
        ),
        CubiculosDto(
            cubiculoId = 4,
            nombre = "Cubículo #4",
            disponible = false
        ),
        CubiculosDto(
            cubiculoId = 5,
            nombre = "Cubículo #5",
            disponible = true
        ),
        CubiculosDto(
            cubiculoId = 6,
            nombre = "Cubículo #6",
            disponible = false
        )
    )

    MaterialTheme {
        DashboardCubiculoScreen(navController = rememberNavController(), usuarioDTO = UsuarioDTO())
    }
}