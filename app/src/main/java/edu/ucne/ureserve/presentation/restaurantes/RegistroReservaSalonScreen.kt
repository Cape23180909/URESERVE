package edu.ucne.ureserve.presentation.salones

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegistroReservaSalonScreen(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: () -> Unit,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Registro Salón",
                            color = Color(0xFF023E8A),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "COMPLETAR REGISTRO DE RESERVA PARA $fecha",
                        color = Color(0xFF023E8A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    RegistroReservaSalonForm(
                        fecha = fecha,
                        onCancelarClick = {
                            notificationHandler.showNotification(
                                title = "Formulario Cancelado",
                                message = "La reserva del salón no fue guardada."
                            )
                            onCancelarClick()
                        },
                        onConfirmarClick = { nombres, apellidos, telefono, matricula, cedula, correoElectronico, direccion ->
                            DatosPersonalesSalonStore.lista.add(
                                DatosPersonalesSalon(
                                    nombres = nombres,
                                    apellidos = apellidos,
                                    telefono = telefono,
                                    matricula = matricula,
                                    cedula = cedula,
                                    correoElectronico = correoElectronico,
                                    direccion = direccion,
                                    fecha = fecha
                                )
                            )
                            viewModel.setFecha(fecha)
                            notificationHandler.showNotification(
                                title = "Reserva Confirmada",
                                message = "La reserva del salón a nombre de $nombres fue registrada correctamente."
                            )
                            onConfirmarClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RegistroReservaSalonForm(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: (
        nombres: String,
        apellidos: String,
        telefono: String,
        matricula: String,
        cedula: String,
        correoElectronico: String,
        direccion: String
    ) -> Unit
) {
    var correoElectronico by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        nombres, apellidos, telefono, matricula, cedula, direccion, correoElectronico
    ).all { it.isNotBlank() } && matricula.length == 8 && cedula.length == 11

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = correoElectronico,
            onValueChange = { correoElectronico = it },
            label = { Text("Correo electrónico *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Número de celular *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
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
            label = { Text("Matrícula *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
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
                val clean = newValue.filter { it.isDigit() }.take(11)
                cedula = clean
            },
            label = { Text("Cédula *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección *", color = Color.Black, fontWeight = FontWeight.Bold) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    correoElectronico = ""
                    nombres = ""
                    apellidos = ""
                    telefono = ""
                    matricula = ""
                    cedula = ""
                    direccion = ""
                    onCancelarClick()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023E8A))
            ) {
                Text("CANCELAR", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onConfirmarClick(nombres, apellidos, telefono, matricula, cedula, correoElectronico, direccion)
                },
                enabled = formularioCompleto,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("CONFIRMAR", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistroReservaSalon() {
    MaterialTheme {
        RegistroReservaSalonScreen(
            fecha = "30/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}