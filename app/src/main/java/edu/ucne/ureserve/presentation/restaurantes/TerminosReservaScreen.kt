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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TerminosReservaScreen(
    onAceptarClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

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
                            text = "Términos de Reserva",
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
                    text = "Términos de reservas del salón de reuniones",
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
                    TerminoItem(text = "Las reservas las puede hacer cualquier tipo de persona, sin importar que sea estudiante o no.")
                    TerminoItem(text = "El costo es de \$15,000. Las reservaciones se pueden agendar hasta con un día de antelación e incluso hasta con un año de antelación.")
                    TerminoItem(text = "Se pueden hacer siempre y cuando tengan el espacio disponible. Las reservas duran un día completo.")
                    TerminoItem(text = "El salón de reuniones cuenta con proyectores, pantalla de proyección, música, asientos y mesas.")
                    TerminoItem(text = "Este apartado cuenta con una capacidad máxima de 15 personas.")
                    TerminoItem(text = "Si desea incluir refrigerios, estos serán cargados a la cotización del salón.")
                    TerminoItem(text = "Los refrigerios son preparados en la UCNE. También puede contar con meseros, pero aumenta el costo.")
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
                    text = "Gracias por utilizar | Reserve!",
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
                                message = "Has rechazado los términos de la reserva del salón."
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
                                    message = "Gracias por aceptar los términos de la reserva del salón."
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
fun TerminoItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            modifier = Modifier.padding(top = 4.dp, end = 8.dp),
            fontSize = 16.sp,
            color = Color(0xFF023E8A)
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTerminosReservaScreen() {
    TerminosReservaScreen()
}