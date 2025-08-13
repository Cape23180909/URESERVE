package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReservaSalonScreen(
    fecha: String,
    navController: NavController
) {
    val context = LocalContext.current
    val notificationHandler = setupNotificationPermission(context)

    ReservaSalonContent(
        fecha = fecha,
        navController = navController,
        notificationHandler = notificationHandler
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun setupNotificationPermission(context: android.content.Context): NotificationHandler {
    val notificationHandler = remember { NotificationHandler(context) }
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(Unit) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    return notificationHandler
}

@Composable
private fun ReservaSalonContent(
    fecha: String,
    navController: NavController,
    notificationHandler: NotificationHandler
) {
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
        HeaderSection()
        Spacer(modifier = Modifier.height(24.dp))
        FormSection(
            correoElectronico = correoElectronico,
            onCorreoChange = { correoElectronico = it },
            nombres = nombres,
            onNombresChange = { nombres = it },
            apellidos = apellidos,
            onApellidosChange = { apellidos = it },
            matricula = matricula,
            onMatriculaChange = { matricula = it },
            cedula = cedula,
            onCedulaChange = { cedula = it },
            telefono = telefono,
            onTelefonoChange = { telefono = it },
            direccion = direccion,
            onDireccionChange = { direccion = it },
            textFieldStyle = textFieldStyle
        )
        Spacer(modifier = Modifier.height(24.dp))
        ButtonSection(
            formularioCompleto = formularioCompleto,
            fecha = fecha,
            navController = navController,
            notificationHandler = notificationHandler,
            nombres = nombres,
            apellidos = apellidos,
            cedula = cedula,
            matricula = matricula,
            telefono = telefono,
            correoElectronico = correoElectronico,
            direccion = direccion,
            horaInicio = horaInicio,
            horaFin = horaFin
        )
    }
}

@Composable
private fun HeaderSection() {
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
}

@Composable
private fun FormSection(
    correoElectronico: String,
    onCorreoChange: (String) -> Unit,
    nombres: String,
    onNombresChange: (String) -> Unit,
    apellidos: String,
    onApellidosChange: (String) -> Unit,
    matricula: String,
    onMatriculaChange: (String) -> Unit,
    cedula: String,
    onCedulaChange: (String) -> Unit,
    telefono: String,
    onTelefonoChange: (String) -> Unit,
    direccion: String,
    onDireccionChange: (String) -> Unit,
    textFieldStyle: TextStyle
) {
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

        TextFieldItem(
            value = correoElectronico,
            onValueChange = onCorreoChange,
            label = "Correo electrónico *",
            textFieldStyle = textFieldStyle
        )
        TextFieldItem(
            value = nombres,
            onValueChange = onNombresChange,
            label = "Nombres *",
            textFieldStyle = textFieldStyle
        )
        TextFieldItem(
            value = apellidos,
            onValueChange = onApellidosChange,
            label = "Apellidos *",
            textFieldStyle = textFieldStyle
        )
        TextFieldItem(
            value = telefono,
            onValueChange = onTelefonoChange,
            label = "Teléfono *",
            textFieldStyle = textFieldStyle,
            keyboardType = KeyboardType.Phone
        )
        MatriculaField(matricula, onMatriculaChange, textFieldStyle)
        CedulaField(cedula, onCedulaChange, textFieldStyle)
        TextFieldItem(
            value = direccion,
            onValueChange = onDireccionChange,
            label = "Dirección *",
            textFieldStyle = textFieldStyle
        )
    }
}

@Composable
private fun TextFieldItem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    textFieldStyle: TextStyle,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontWeight = FontWeight.Bold, color = Color.Black) },
        textStyle = textFieldStyle,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MatriculaField(
    matricula: String,
    onMatriculaChange: (String) -> Unit,
    textFieldStyle: TextStyle
) {
    OutlinedTextField(
        value = buildString {
            append(matricula.take(8))
            if (length > 4) insert(4, "-")
        },
        onValueChange = { newValue ->
            onMatriculaChange(newValue.filter { it.isDigit() }.take(8))
        },
        label = { Text("Matrícula *", fontWeight = FontWeight.Bold, color = Color.Black) },
        textStyle = textFieldStyle,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = { Text("XXXX-XXXX", color = Color.Gray) }
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun CedulaField(
    cedula: String,
    onCedulaChange: (String) -> Unit,
    textFieldStyle: TextStyle
) {
    OutlinedTextField(
        value = cedula.run {
            when {
                length <= 3 -> this
                length <= 10 -> "${substring(0, 3)}-${substring(3)}"
                else -> "${substring(0, 3)}-${substring(3, 10)}-${substring(10)}"
            }
        },
        onValueChange = { newValue ->
            onCedulaChange(newValue.filter { it.isDigit() }.take(11))
        },
        label = { Text("Cédula *", fontWeight = FontWeight.Bold, color = Color.Black) },
        textStyle = textFieldStyle,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = { Text("XXX-XXXXXXX-X", color = Color.Gray) }
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ButtonSection(
    formularioCompleto: Boolean,
    fecha: String,
    navController: NavController,
    notificationHandler: NotificationHandler,
    nombres: String,
    apellidos: String,
    cedula: String,
    matricula: String,
    telefono: String,
    correoElectronico: String,
    direccion: String,
    horaInicio: String,
    horaFin: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                handleCancelClick(
                    notificationHandler = notificationHandler,
                    onCancelarClick = { navController.popBackStack() }
                )
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
        ) {
            Text("CANCELAR")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {
                handleConfirmClick(
                    fecha = fecha,
                    navController = navController,
                    notificationHandler = notificationHandler,
                    nombres = nombres,
                    apellidos = apellidos,
                    cedula = cedula,
                    matricula = matricula,
                    telefono = telefono,
                    correoElectronico = correoElectronico,
                    direccion = direccion,
                    horaInicio = horaInicio,
                    horaFin = horaFin
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

private fun handleCancelClick(
    notificationHandler: NotificationHandler,
    onCancelarClick: () -> Unit
) {
    notificationHandler.showNotification(
        title = "Reserva cancelada",
        message = "Has cancelado el registro de reserva del salón."
    )
    onCancelarClick()
}

private fun handleConfirmClick(
    fecha: String,
    navController: NavController,
    notificationHandler: NotificationHandler,
    nombres: String,
    apellidos: String,
    cedula: String,
    matricula: String,
    telefono: String,
    correoElectronico: String,
    direccion: String,
    horaInicio: String,
    horaFin: String
) {
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
}