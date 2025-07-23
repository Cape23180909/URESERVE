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
                text = "Registro Sala VIP",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.sala),
                contentDescription = "Sala VIP",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Formulario para $fecha",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RegistroSalaVipForm(
            fecha = fecha,
            onCancelarClick = onCancelarClick,
            onConfirmarClick = { correoElectronico, nombres, apellidos, telefono, matricula, cedula, direccion ->
                DatosPersonalesSalaVipStore.lista.add(
                    DatosPersonalesSalaVip(
                        correoElectronico = correoElectronico,
                        nombre = nombres,
                        apellidos = apellidos,
                        telefono = telefono,
                        matricula = matricula,
                        cedula = cedula,
                        direccion = direccion
                    )
                )

                viewModel.setNombres(nombres)
                viewModel.setDireccion(direccion)
                viewModel.setTelefono(telefono)
                viewModel.setCorreo(correoElectronico)
                viewModel.setFecha(fecha)

                onConfirmarClick()
            }
        )
    }
}

@Composable
fun RegistroSalaVipForm(
    fecha: String,
    onCancelarClick: () -> Unit,
    onConfirmarClick: (
        correoElectronico: String,
        nombres: String,
        apellidos: String,
        telefono: String,
        matricula: String,
        cedula: String,
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
        correoElectronico, nombres, apellidos, telefono, matricula, cedula, direccion
    ).all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Datos de la Sala VIP",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correoElectronico,
            onValueChange = { correoElectronico = it },
            label = { Text("Correo Electronico *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it},
            label = { Text("apellidos *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = {
                // Filtra solo números y limita a 8 dígitos
                val cleaned = it.filter { char -> char.isDigit() }.take(8)

                matricula = when {
                    cleaned.length <= 4 -> cleaned
                    else -> "${cleaned.take(4)}-${cleaned.drop(4)}"
                }
            },
            label = { Text("Matrícula *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cedula,
            onValueChange = { input ->
                // Filtra solo dígitos
                val digits = input.filter { it.isDigit() }.take(11)

                // Aplica formato: xxx-xxxxxxx-x
                cedula = when {
                    digits.length <= 3 -> digits
                    digits.length <= 10 -> "${digits.take(3)}-${digits.drop(3).take(7)}"
                    else -> "${digits.take(3)}-${digits.drop(3).take(7)}-${digits.drop(10)}"
                }
            },
            label = { Text("Cédula *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Direccion *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
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
                modifier = Modifier.weight(1f)
            ) {
                Text("CANCELAR")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onConfirmarClick(correoElectronico, nombres, apellidos, telefono, matricula, cedula, direccion)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistroReservaScreen() {
    MaterialTheme {
        RegistroReservaScreen(
            fecha = "30/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}