package edu.ucne.ureserve.presentation.salones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            Spacer(Modifier.size(40.dp))
            Text(
                text = "Registro Salón",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(40.dp))
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
        Text(
            text = "Datos del Salón",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = correoElectronico,
            onValueChange = { correoElectronico = it },
            label = { Text("Correo electrónico *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos *") },
            modifier = Modifier.fillMaxWidth()
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
            value = buildString {
                append(matricula.take(8))
                if (length > 4) insert(4, "-")
            },
            onValueChange = { newValue ->
                matricula = newValue.filter { it.isDigit() }.take(8)
            },
            label = { Text("Matrícula *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("XXXX-XXXX") }
        )

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
            label = { Text("Cédula *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("XXX-XXXXXXX-X") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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
                        nombres,
                        apellidos,
                        telefono,
                        matricula,
                        cedula,
                        correoElectronico,
                        direccion
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
    androidx.compose.material3.MaterialTheme {
        RegistroReservaSalonScreen(
            fecha = "30/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}