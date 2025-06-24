package edu.ucne.ureserve.presentation.restaurantes

import android.app.DatePickerDialog
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.*

@Composable
fun TarjetaCreditoSalaVipScreen(
    fecha: String,
    navController: NavController
) {
    var numeroTarjeta by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var codigoSeguridad by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, _ ->
                val mes = String.format("%02d", selectedMonth + 1)
                val anno = selectedYear.toString().takeLast(2)
                fechaVencimiento = "$mes$anno"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = calendar.timeInMillis
        }
    }

    val dateFilter = remember {
        object : VisualTransformation {
            override fun filter(text: AnnotatedString): TransformedText {
                val numericText = text.text.filter { it.isDigit() }
                val trimmed = numericText.take(4)
                val out = buildString {
                    for (i in trimmed.indices) {
                        append(trimmed[i])
                        if (i == 1 && trimmed.length > 2) append("/")
                    }
                }
                return TransformedText(
                    AnnotatedString(out),
                    object : OffsetMapping {
                        override fun originalToTransformed(offset: Int) = when {
                            offset <= 1 -> offset
                            offset <= 3 -> offset + 1
                            else -> 5
                        }

                        override fun transformedToOriginal(offset: Int) = when {
                            offset <= 2 -> offset
                            offset <= 5 -> offset - 1
                            else -> 4
                        }
                    }
                )
            }
        }
    }

    val isCardNumberValid = numeroTarjeta.length == 16
    val isNameValid = nombreTitular.isNotBlank()
    val isFechaValid = fechaVencimiento.length == 4 && fechaVencimiento.take(2).toIntOrNull() in 1..12
    val isCodigoSeguridadValid = codigoSeguridad.length in 3..4 && codigoSeguridad.all { it.isDigit() }
    val isFormValid = isCardNumberValid && isNameValid && isFechaValid && isCodigoSeguridadValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SALA VIP",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF023E8A),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Datos de pago", style = MaterialTheme.typography.titleLarge, color = Color(0xFF023E8A))
        Text("Tarjeta de crédito", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numeroTarjeta,
            onValueChange = {
                val digitsOnly = it.filter { ch -> ch.isDigit() }
                numeroTarjeta = digitsOnly.take(16)
            },
            label = { Text("Número de Tarjeta") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CreditCardFilter,
            textStyle = TextStyle(
                color = if (numeroTarjeta.length >= 14) Color(0xFF2E7D32) else Color.Unspecified
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (numeroTarjeta.length >= 14) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (numeroTarjeta.length >= 14) Color(0xFF2E7D32) else Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreTitular,
            onValueChange = { nombreTitular = it },
            label = { Text("Nombre del titular de la tarjeta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = fechaVencimiento,
                onValueChange = {
                    val filtered = it.filter { c -> c.isDigit() }
                    fechaVencimiento = filtered.take(4)
                },
                label = { Text("Fecha de vencimiento") },
                placeholder = { Text("MM/AA") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = dateFilter,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            OutlinedTextField(
                value = codigoSeguridad,
                onValueChange = {
                    val filtered = it.filter { ch -> ch.isDigit() }
                    codigoSeguridad = filtered.take(4)
                },
                label = { Text("Código de seguridad") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Text(
            text = "3 dígitos en el reverso de la tarjeta\n4 dígitos en el frente*",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF023E8A)
                )
            ) {
                Text("CANCELAR")
            }

            Button(
                onClick = {
                    if (isFormValid) {
                        val numeroReserva = (1000..9999).random().toString()
                        DatosPersonalesStore.lista.clear()
                        navController.navigate("ReservaSalaVipExitosa?numeroReserva=$numeroReserva")
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF388E3C) else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("CONFIRMAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

val CreditCardFilter = VisualTransformation { text ->
    val trimmed = text.text.take(16)
    val spaced = buildString {
        for (i in trimmed.indices) {
            append(trimmed[i])
            if (i == 3 || i == 7 || i == 11) append(" ")
        }
    }
    TransformedText(
        AnnotatedString(spaced),
        object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when (offset) {
                in 0..3 -> offset
                in 4..7 -> offset + 1
                in 8..11 -> offset + 2
                in 12..15 -> offset + 3
                else -> offset + 4
            }

            override fun transformedToOriginal(offset: Int): Int = when (offset) {
                in 0..4 -> offset
                in 5..9 -> offset - 1
                in 10..14 -> offset - 2
                in 15..19 -> offset - 3
                else -> offset - 4
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTarjetaCreditoSalaVipScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        TarjetaCreditoSalaVipScreen(
            fecha = "23/06/2025",
            navController = navController
        )
    }
}
