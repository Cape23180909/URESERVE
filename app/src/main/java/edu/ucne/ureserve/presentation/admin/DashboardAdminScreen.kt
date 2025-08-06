package edu.ucne.ureserve.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

@Composable
fun DashboardAdminScreen(
    onOpcionesEmpleadoProyector: () -> Unit = {},
    onReportes: () -> Unit = {},
    onBuscarReservas: () -> Unit = {},
    onLogout: () -> Unit = {},
    navController: NavController,
    usuario: UsuarioDTO
) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD9D9D9))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        MenuItem(
            iconRes = R.drawable.icon_adminsettings,
            text = "Opciones de\nadministrador",
            onClick = {
                when (usuario.correoInstitucional) {
                    "jacksonperez@gmail.com" -> navController.navigate("empleadoproyecto")
                    "richardbautista@gmail.com" -> navController.navigate("empleadolaboratorio")
                    "yandelwisin@gmail.com" -> navController.navigate("empleadoCubiculo")
                    "restauranteencargado@gmail.com" -> navController.navigate("empleadoRestaurante")
                    else -> {}
                }
            },
            circleWidth = 130.dp,
            circleHeight = 160.dp,
            iconWidth = 110.dp,
            iconHeight = 110.dp,
            rowAlignment = Arrangement.Start,
            rowWidthFraction = 0.94f
        )

        MenuItem(
            iconRes = R.drawable.icon_reporte,
            text = "Reportes",
            onClick = onReportes,
            circleWidth = 132.dp,
            circleHeight = 124.dp,
            iconWidth = 90.dp,
            iconHeight = 90.dp
        )

        MenuItem(
            iconRes = R.drawable.icon_search,
            text = "Buscar\nReservas",
            onClick = onBuscarReservas,
            circleWidth = 132.dp,
            circleHeight = 124.dp,
            iconWidth = 90.dp,
            iconHeight = 90.dp,
            rowAlignment = Arrangement.Start,
            rowWidthFraction = 0.94f,
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                notificationHandler.showNotification(
                    title = "Sesión Cerrada",
                    message = "Has cerrado sesión correctamente."
                )
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF457BD1),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(42.dp)
        ) {
            Text(text = "Cerrar sesion", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MenuItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    circleWidth: Dp = 172.dp,
    circleHeight: Dp = 162.dp,
    iconWidth: Dp = 101.dp,
    iconHeight: Dp = 104.dp,
    rowAlignment: Arrangement.Horizontal = Arrangement.Center,
    rowWidthFraction: Float = 1f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(rowWidthFraction)
            .padding(vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = rowAlignment,
    ) {
        Box(
            modifier = Modifier
                .width(circleWidth)
                .height(circleHeight)
                .clip(CircleShape)
                .background(Color(0xFFFFDF00)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .width(iconWidth)
                    .height(iconHeight)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD9D9D9))
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDashboardAdminScreen() {
    val navController = rememberNavController()
    val mockUsuario = UsuarioDTO(
        usuarioId = 1,
        nombres = "Jackson",
        apellidos = "Pérez",
        correoInstitucional = "jacksonperez@gmail.com"
    )

    MaterialTheme {
        DashboardAdminScreen(
            onOpcionesEmpleadoProyector = {},
            onReportes = {},
            onBuscarReservas = {},
            onLogout = {},
            navController = navController,
            usuario = mockUsuario
        )
    }
}