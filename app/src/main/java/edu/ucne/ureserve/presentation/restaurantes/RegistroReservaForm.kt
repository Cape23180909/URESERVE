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
            onConfirmarClick = { nombre, ubicacion, capacidad, telefono, correo, descripcion ->
                DatosPersonalesSalaVipStore.lista.add(
                    DatosPersonalesSalaVip(
                        nombre = nombre,
                        ubicacion = ubicacion,
                        capacidad = capacidad.toIntOrNull() ?: 0,
                        telefono = telefono,
                        correo = correo,
                        descripcion = descripcion,
                        fecha = fecha
                    )
                )

                viewModel.setNombre(nombre)
                viewModel.setUbicacion(ubicacion)
                viewModel.setCapacidad(capacidad.toIntOrNull() ?: 0)
                viewModel.setTelefono(telefono)
                viewModel.setCorreo(correo)
                viewModel.setDescripcion(descripcion)
                viewModel.setFecha(fecha)

                viewModel.create()

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
        nombre: String,
        ubicacion: String,
        capacidad: String,
        telefono: String,
        correo: String,
        descripcion: String
    ) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        nombre, ubicacion, capacidad, telefono, correo, descripcion
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
            value = capacidad,
            onValueChange = { capacidad = it.filter { c -> c.isDigit() } },
            label = { Text("Capacidad *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
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
                    capacidad = ""
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
                    onConfirmarClick(nombre, ubicacion, capacidad, telefono, correo, descripcion)
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
