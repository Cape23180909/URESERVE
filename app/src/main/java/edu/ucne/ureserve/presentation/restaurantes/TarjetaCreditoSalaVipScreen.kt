package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.app.DatePickerDialog
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TarjetaCreditoSalaVipScreen(
    fecha: String,
    navController: NavController
) {
    val context = LocalContext.current
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null
    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    var numeroTarjeta by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var codigoSeguridad by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, _ ->
                val mes = String.format("%02d", month + 1)
                val anno = year.toString().takeLast(2)
                fechaVencimiento = "$mes$anno"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = calendar.timeInMillis
        }
    }

    val isCardValid = numeroTarjeta.length == 16
    val isNameValid = nombreTitular.trim().isNotEmpty()
    val isFechaValid = fechaVencimiento.length == 4 && fechaVencimiento.take(2).toIntOrNull() in 1..12
    val isCvvValid = codigoSeguridad.length in 3..4 && codigoSeguridad.all { it.isDigit() }
    val isFormValid = isCardValid && isNameValid && isFechaValid && isCvvValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = "Datos de pago",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tarjeta de crédito",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numeroTarjeta,
                onValueChange = {
                    val digits = it.filter { it.isDigit() }.take(16)
                    numeroTarjeta = digits
                },
                label = { Text("Número de Tarjeta", fontWeight = FontWeight.Bold, color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = CreditCardVisualTransformation,
                singleLine = true,
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombreTitular,
                onValueChange = { nombreTitular = it },
                label = { Text("Nombre del titular de la tarjeta", fontWeight = FontWeight.Bold, color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaVencimiento,
                    onValueChange = {
                        val filtered = it.filter { it.isDigit() }.take(4)
                        fechaVencimiento = filtered
                    },
                    label = { Text("Fecha de vencimiento", fontWeight = FontWeight.Bold, color = Color.Black) },
                    placeholder = { Text("MM / AA") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = FechaVisualTransformation,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = LocalTextStyle.current.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = codigoSeguridad,
                    onValueChange = {
                        val digits = it.filter { it.isDigit() }.take(4)
                        codigoSeguridad = digits
                    },
                    label = { Text("Código de seguridad", fontWeight = FontWeight.Bold, color = Color.Black) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    maxLines = 1,
                    textStyle = LocalTextStyle.current.copy(color = Color.Black)
                )
            }
            Text(
                text = "3 dígitos en el reverso de la tarjeta\n4 dígitos en el anverso de la tarjeta*",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        notificationHandler.showNotification(
                            title = "Reserva Cancelada",
                            message = "Has cancelado el registro de pago."
                        )
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004BBB)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCELAR", color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (isFormValid) {
                            notificationHandler.showNotification(
                                title = "Pago Confirmado",
                                message = "Los datos de tu tarjeta han sido registrados correctamente."
                            )
                            navController.navigate("ReservaSalaVip")
                            navController.navigate("RegistroReservaSalaVip?fecha=$fecha")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF00B81D) else Color(0xFF6895D2)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CONFIRMAR", color = Color.White)
                }
            }
        }
    }
}

val CreditCardVisualTransformation = VisualTransformation { text ->
    val trimmed = text.text.take(16)
    val spaced = buildString {
        for (i in trimmed.indices) {
            append(trimmed[i])
            if ((i + 1) % 4 == 0 && i != 15) append(" ")
        }
    }
    val offsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return offset + (offset / 4).coerceAtMost(3)
        }
        override fun transformedToOriginal(offset: Int): Int {
            return offset - (offset / 5).coerceAtMost(3)
        }
    }
    TransformedText(AnnotatedString(spaced), offsetTranslator)
}

val FechaVisualTransformation = VisualTransformation { text ->
    val trimmed = text.text.take(4)
    val formatted = buildString {
        for (i in trimmed.indices) {
            append(trimmed[i])
            if (i == 1 && trimmed.length > 2) append(" / ")
        }
    }
    val offsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return if (offset <= 1) offset else offset + 3
        }
        override fun transformedToOriginal(offset: Int): Int {
            return if (offset <= 2) offset else offset - 3
        }
    }
    TransformedText(AnnotatedString(formatted), offsetTranslator)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTarjetaCreditoSalaVipScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        TarjetaCreditoSalaVipScreen(
            fecha = "1226",
            navController = navController
        )
    }
}