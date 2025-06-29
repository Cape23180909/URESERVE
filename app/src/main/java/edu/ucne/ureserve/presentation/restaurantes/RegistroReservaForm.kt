// Archivo: RegistroReservaScreen.kt
package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.ureserve.R

// Composable principal de la pantalla
@Composable
fun RegistroReservaScreen(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: () -> Unit,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
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
                text = "Pago Sala VIP",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.sala),
                contentDescription = "Sala",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Formulario para $fecha",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RegistroReservaForm(
            onCancelarClick = onCancelarClick,
            onConfirmarClick = {
                viewModel.create()  // Ejecuta la acción en ViewModel para crear
                onConfirmarClick()  // Callback externo si lo necesitas
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun RegistroReservaForm(
    onCancelarClick: () -> Unit,
    onConfirmarClick: () -> Unit,
    viewModel: RestaurantesViewModel
) {
    // Aquí se pueden usar variables locales para controlar inputs o directamente usar uiState
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var capacidadStr by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val capacidad = capacidadStr.toIntOrNull() ?: 0

    val formularioCompleto = listOf(
        nombre, ubicacion, telefono, correo, descripcion
    ).all { it.isNotBlank() } && capacidad > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Registro de Restaurante",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = capacidadStr,
            onValueChange = { input -> capacidadStr = input.filter { it.isDigit() } },
            label = { Text("Capacidad *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    nombre = ""
                    ubicacion = ""
                    capacidadStr = ""
                    telefono = ""
                    correo = ""
                    descripcion = ""
                    onCancelarClick()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("CANCELAR")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.setNombre(nombre)
                    viewModel.setUbicacion(ubicacion)
                    viewModel.setCapacidad(capacidad)
                    viewModel.setTelefono(telefono)
                    viewModel.setCorreo(correo)
                    viewModel.setDescripcion(descripcion)
                    viewModel.setFecha("") // o la fecha que tengas
                    onConfirmarClick()
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

        // Mostrar errores si los tienes en el ViewModel
        viewModel.uiState.collectAsState().value.inputError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistroReservaScreen() {
    MaterialTheme {
        RegistroReservaScreen(
            fecha = "15/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}
