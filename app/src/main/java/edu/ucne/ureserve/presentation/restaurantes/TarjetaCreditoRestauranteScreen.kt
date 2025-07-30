package edu.ucne.ureserve.presentation.restaurantes

import android.app.DatePickerDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.*

@Composable
fun TarjetaCreditoRestauranteScreen(
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

    // Validaciones
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
                .background(Color(0xFFF1F1F1), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = "Datos de pago",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Tarjeta de crédito",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numeroTarjeta,
                onValueChange = {
                    val digits = it.filter { it.isDigit() }.take(16)
                    numeroTarjeta = digits
                },
                label = { Text("Número de Tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = CreditCardVisualTransformations,
                singleLine = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombreTitular,
                onValueChange = { nombreTitular = it },
                label = { Text("Nombre del titular de la tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaVencimiento,
                    onValueChange = {
                        val filtered = it.filter { it.isDigit() }.take(4)
                        fechaVencimiento = filtered
                    },
                    label = { Text("Fecha de vencimiento") },
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
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = codigoSeguridad,
                    onValueChange = {
                        val digits = it.filter { it.isDigit() }.take(4)
                        codigoSeguridad = digits
                    },
                    label = { Text("Código de seguridad") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    maxLines = 1
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
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCELAR")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        // Navegar a Registro de Reserva
                        navController.navigate("RegistroReservaRestaurante")
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF2E7D32) else Color.Gray
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CONFIRMAR")
                }
            }
        }
    }
}

// VisualTransformation para mostrar espacios cada 4 dígitos en la tarjeta
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

// VisualTransformation para mostrar fecha MM / AA
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