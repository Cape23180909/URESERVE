package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TarjetaCreditoRestauranteScreen(
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
                visualTransformation = CreditCardVisualTransformations,
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
                    visualTransformation = FechaVisualTransformations,
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
                            title = "Pago cancelado",
                            message = "Has cancelado el pago con tarjeta de crédito."
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
                                title = "Pago confirmado",
                                message = "Pago confirmado para la fecha $fecha"
                            )
                            navController.navigate("RegistroReservaRestaurante")
                            navController.navigate("RegistroReservaRestaurante?fecha=${Uri.encode(fecha)}")
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

val CreditCardVisualTransformations = VisualTransformation { text ->
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

    TransformedText(
        AnnotatedString(spaced),
        offsetTranslator
    )
}

val FechaVisualTransformations = VisualTransformation { text ->
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

    TransformedText(
        AnnotatedString(formatted),
        offsetTranslator
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTarjetaCreditoRestauranteScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        TarjetaCreditoRestauranteScreen(
            fecha = "0625",
            navController = navController
        )
    }
}