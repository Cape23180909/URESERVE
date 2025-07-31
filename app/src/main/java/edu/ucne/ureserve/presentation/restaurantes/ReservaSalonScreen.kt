package edu.ucne.ureserve.presentation.salones

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReservaSalonScreen(
    fecha: String,
    onCancelarClick: () -> Unit = {},
    onConfirmarClick: () -> Unit = {},
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
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

    var correoElectronico by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        nombres, apellidos, matricula, cedula, telefono, correoElectronico, direccion
    ).all { it.isNotBlank() }

    val textFieldStyle = TextStyle(color = Color.Black)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Registro de reserva Salón",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "COMPLETAR REGISTRO DE RESERVA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = { Text("Correo electrónico *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = { Text("Nombres *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = buildString {
                    append(matricula.take(8))
                    if (length > 4) insert(4, "-")
                },
                onValueChange = { newValue ->
                    matricula = newValue.filter { it.isDigit() }.take(8)
                },
                label = { Text("Matrícula *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("XXXX-XXXX", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cedula.run {
                    when {
                        length <= 3 -> this
                        length <= 10 -> "${substring(0, 3)}-${substring(3)}"
                        else -> "${substring(0, 3)}-${substring(3, 10)}-${substring(10)}"
                    }
                },
                onValueChange = { newValue ->
                    cedula = newValue.filter { it.isDigit() }.take(11)
                },
                label = { Text("Cédula *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("XXX-XXXXXXX-X", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección *", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = textFieldStyle,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        notificationHandler.showNotification(
                            title = "Reserva cancelada",
                            message = "Has cancelado el registro de reserva del salón."
                        )
                        nombres = ""
                        apellidos = ""
                        cedula = ""
                        matricula = ""
                        telefono = ""
                        correoElectronico = ""
                        direccion = ""
                        onCancelarClick()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
                ) {
                    Text("CANCELAR")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        DatosPersonalesSalonStore.lista.add(
                            DatosPersonalesSalon(
                                nombres = nombres,
                                apellidos = apellidos,
                                cedula = cedula,
                                matricula = matricula,
                                telefono = telefono,
                                correoElectronico = correoElectronico,
                                direccion = direccion,
                                fecha = fecha,
                                horaInicio = horaInicio,
                                horaFin = horaFin
                            )
                        )
                        notificationHandler.showNotification(
                            title = "Reserva confirmada",
                            message = "Tu reserva del salón fue registrada para la fecha $fecha."
                        )
                        navController.navigate("PagoSalon?fecha=${Uri.encode(fecha)}")
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
}