package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RestauranteScreen(
    navController: NavHostController,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onBottomNavClick: (String) -> Unit = {},
    onExclamacionClick: () -> Unit = {},
    terminosAceptados: Boolean = false
) {
    val context = LocalContext.current


    // Solicitud de permiso para notificaciones en Android 13+
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else null

    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.comer),
                contentDescription = "Ícono Restaurante",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Restaurante",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen del restaurante
        Image(
            painter = painterResource(id = R.drawable.cafeteria),
            contentDescription = "Imagen Restaurante",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.esclamacion),
                contentDescription = "Icono de exclamación",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        notificationHandler.showNotification(
                            title = "Información importante",
                            message = "Por favor, lee las condiciones antes de reservar."
                        )
                        onExclamacionClick()
                    },
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Leer antes de realizar cualquier reserva.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    notificationHandler.showNotification(
                        title = "Reserva cancelada",
                        message = "Has cancelado el proceso de reserva en el restaurante."
                    )
                    onCancelClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "CANCELAR", color = Color.White)
            }


            Button(
                onClick = {
                    notificationHandler.showNotification(
                        title = "Reserva confirmada",
                        message = if (terminosAceptados)
                            "Has confirmado la reserva con los términos aceptados."
                        else
                            "Confirma los términos antes de continuar."
                    )
                    onConfirmClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (terminosAceptados) Color(0xFF388E3C) else Color(0xFF2196F3)
                )
            ) {
                Text(text = "CONFIRMAR", color = Color.White)
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        // Barra de navegación inferior
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
fun BottomNavItem(iconRes: Int, label: String, onClick: () -> Unit) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }

    Column(
        modifier = Modifier.clickable { notificationHandler.showNotification(
            title = "Navegación",
            message = "Has navegado a: $label"
        )
            onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRestauranteScreen() {
    // Necesitarás un NavController mock para la vista previa
    val navController = rememberNavController()
    RestauranteScreen(
        navController = navController,
        onBottomNavClick = {}
    )

}