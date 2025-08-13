package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SalaVipTransferenciaScreen(
    fecha: String,
    onCancelarClick: () -> Unit = {},
    onTransferenciaClick: (String) -> Unit = {},
    onConfirmarClick: (String) -> Unit = {}
) {

    val transferSelectedTitle = "Transferencia seleccionada"
    val confirmationTitle = "Confirmación"
    val cancelledTitle = "Cancelado"

    val bancoDedLobo1 = "Banco DED Lobo - 979-291390283"
    val bancoDedLobo2 = "Banco DED Lobo - 999-100529-2"
    val bancoPopular = "Banco Popular - 71936351-5"

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
    var bancoSeleccionado by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Pago Sala VIP - Transferencia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Seleccione el banco para transferencia",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            bancoSeleccionado = bancoDedLobo1
                            onTransferenciaClick(bancoSeleccionado)
                            notificationHandler.showNotification(
                                title = transferSelectedTitle,
                                message = "Has elegido transferir a $bancoDedLobo1."
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Transferir a $bancoDedLobo1")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            bancoSeleccionado = bancoDedLobo2
                            onTransferenciaClick(bancoSeleccionado)
                            notificationHandler.showNotification(
                                title = transferSelectedTitle,
                                message = "Has elegido transferir a $bancoDedLobo2."
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Transferir a $bancoDedLobo2")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            bancoSeleccionado = bancoPopular
                            onTransferenciaClick(bancoSeleccionado)
                            notificationHandler.showNotification(
                                title = transferSelectedTitle,
                                message = "Has elegido transferir a $bancoPopular."
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Transferir a $bancoPopular")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Colocar materiales al momento de efectuar el pago, enviar comprobante al correo electrónico:",
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                onConfirmarClick(fecha)
                                notificationHandler.showNotification(
                                    title = confirmationTitle,
                                    message = "Tu transferencia ha sido confirmada para la fecha $fecha."
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            enabled = bancoSeleccionado.isNotEmpty()
                        ) {
                            Text(text = "CONFIRMAR", fontSize = 16.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                onCancelarClick()
                                notificationHandler.showNotification(
                                    title = cancelledTitle,
                                    message = "Has cancelado el proceso de transferencia."
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CANCELAR")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalaVipTransferenciaPreview() {
    SalaVipTransferenciaScreen(
        fecha = "26/06/2025",
        onCancelarClick = {},
        onTransferenciaClick = {},
        onConfirmarClick = {}
    )
}