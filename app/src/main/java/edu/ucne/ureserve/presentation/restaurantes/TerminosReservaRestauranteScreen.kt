package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TerminosReservaRestauranteScreen(
    onAceptarClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Permiso notificaciones
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

    var aceptado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
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
                        Text(
                            text = "Términos de Reserva - Restaurante",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF023E8A)),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.atras),
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Términos de reservas del restaurante",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    TerminoItem("Las reservas las puede hacer cualquier tipo de persona, sin importar que sea estudiante o no.")
                    TerminoItem("El costo es de \$25,000. Las reservaciones se pueden agendar hasta con un día de antelación e incluso hasta con un año de antelación.")
                    TerminoItem("Se pueden hacer siempre y cuando tengan el espacio disponible. Las reservas duran un día completo.")
                    TerminoItem("El Restaurante cuenta con proyectores, pantalla de proyección, música, asientos y mesas.")
                    TerminoItem("Este apartado cuenta con una capacidad máxima de 35 personas.")
                    TerminoItem("Si desea incluir refrigerios, estos serán cargados a la cotización del salón.")
                    TerminoItem("Los refrigerios son preparados en la UCNE. También puede contar con meseros, pero aumenta el costo.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = aceptado,
                        onCheckedChange = { aceptado = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF023E8A),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estoy de acuerdo con estos términos",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Gracias por utilizar UReserve!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            notificationHandler.showNotification(
                                title = "Términos rechazados",
                                message = "Has rechazado los términos de la reserva."
                            )
                            onCancelarClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text(text = "RECHAZAR", color = Color.White)
                    }

                    if (aceptado) {
                        Button(
                            onClick = {
                                notificationHandler.showNotification(
                                    title = "Términos aceptados",
                                    message = "Gracias por aceptar los términos de la reserva."
                                )
                                onAceptarClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                        ) {
                            Text(text = "ACEPTAR", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}

@Composable
fun TerminoItem(
    text: String,
    modifier: Modifier = Modifier,
    bulletColor: Color = Color(0xFF023E8A),
    textColor: Color = Color.Black,
    bulletSize: TextUnit = 16.sp,
    textSize: TextUnit = 16.sp,
    verticalPadding: Dp = 8.dp,
    horizontalPadding: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding, horizontal = horizontalPadding),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            modifier = Modifier.padding(top = 4.dp, end = 8.dp),
            fontSize = bulletSize,
            color = bulletColor
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = textSize,
            color = textColor,
            lineHeight = textSize * 1.25f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTerminosReservaRestauranteScreen() {
    TerminosReservaRestauranteScreen()
}