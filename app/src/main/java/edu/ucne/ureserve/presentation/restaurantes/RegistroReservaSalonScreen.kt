package edu.ucne.ureserve.presentation.salones

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel

@Composable
fun RegistroReservaSalonScreen(
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
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = "Registro Salón",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Text(
            text = "Formulario para $fecha",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RegistroReservaSalonForm(
            fecha = fecha,
            onCancelarClick = onCancelarClick,
            onConfirmarClick = { nombre, ubicacion, capacidad, telefono, correo, descripcion ->
                // Guardar en Store
                DatosPersonalesSalonStore.lista.add(
                    DatosPersonalesSalon(
                        nombre = nombre,
                        ubicacion = ubicacion,
                        capacidad = capacidad,
                        telefono = telefono,
                        correo = correo,
                        descripcion = descripcion,
                        fecha = fecha
                    )
                )

                viewModel.setFecha(fecha)
                viewModel.create()

                onConfirmarClick()
            }
        )
    }
}

@Composable
private fun RegistroReservaSalonForm(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: (
        nombre: String,
        ubicacion: String,
        capacidad: Int,
        telefono: String,
        correo: String,
        descripcion: String
    ) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var capacidadStr by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val capacidad = capacidadStr.toIntOrNull() ?: 0
    val formularioCompleto = listOf(
        nombre, ubicacion, capacidadStr, telefono, correo, descripcion
    ).all { it.isNotBlank() } && capacidad > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Datos del Salón",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre *") },
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
            value = capacidadStr,
            onValueChange = { input -> capacidadStr = input.filter { it.isDigit() } },
            label = { Text("Capacidad *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                        nombre,
                        ubicacion,
                        capacidad,
                        telefono,
                        correo,
                        descripcion
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

@Preview(showBackground = true)
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
