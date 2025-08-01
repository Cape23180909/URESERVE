package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegistroReservaRestauranteScreen(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: () -> Unit,
    viewModel: RestaurantesViewModel = hiltViewModel()
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Registro Restaurante",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.comer),
                contentDescription = "Restaurante",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Formulario para $fecha",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RegistroReservaRestauranteForm(
            fecha = fecha,
            onCancelarClick = {
                //  Notificación al cancelar
                notificationHandler.showNotification(
                    title = "Reserva Cancelada",
                    message = "No se ha guardado la reserva del restaurante."
                )
                onCancelarClick()
            },
            onConfirmarClick = { nombre, direccion, capacidad, telefono, correo, descripcion ->
                DatosPersonalesSalaVipStore.lista.add(
                    DatosPersonalesSalaVip(
                        correoElectronico = correo,
                        nombre = nombre,
                        direccion = direccion,
                        telefono = telefono,
                        descripcion = descripcion,
                        fecha = fecha
                    )
                )

                viewModel.setFecha(fecha)

                notificationHandler.showNotification(
                    title = "Reserva Confirmada",
                    message = "Tu reserva en $nombre ha sido registrada exitosamente."
                )
                onConfirmarClick()
            }
        )
    }
}

@Composable
private fun RegistroReservaRestauranteForm(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: (
        nombre: String,
        ubicacion: String,
        capacidad: String,
        telefono: String,
        correo: String,
        descripcion: String
    ) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        nombre, ubicacion, capacidad, telefono, correo, descripcion
    ).all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Datos de la Reserva",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Restaurante *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = capacidad,
            onValueChange = { capacidad = it },
            label = { Text("Capacidad *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancelarClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("CANCELAR")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    onConfirmarClick(
                        nombre, ubicacion, capacidad,
                        telefono, correo, descripcion
                    )
                },
                enabled = formularioCompleto,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formularioCompleto) Color(0xFF388E3C) else Color.Gray
                )
            ) {
                Text("CONFIRMAR", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistroReservaRestaurante() {
    MaterialTheme {
        RegistroReservaRestauranteScreen(
            fecha = "20/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}