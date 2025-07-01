package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.ureserve.R

@Composable
fun ReservaSalaVipScreen(
    fecha: String,
    onCancelarClick: () -> Unit = {},
    onConfirmarClick: () -> Unit = {},
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
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
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

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
                text = "Registro de reserva",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                onValueChange = { capacidadStr = it.filter { it.isDigit() } },
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
                Button(
                    onClick = {
                        nombre = ""
                        ubicacion = ""
                        capacidadStr = ""
                        telefono = ""
                        correo = ""
                        descripcion = ""
                        onCancelarClick()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
                ) {
                    Text("CANCELAR")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        // Guardar en el Store correcto
                        DatosPersonalesSalaVipStore.lista.add(
                            DatosPersonalesSalaVip(
                                nombre = nombre,
                                ubicacion = ubicacion,
                                capacidad = capacidad,
                                telefono = telefono,
                                correo = correo,
                                descripcion = descripcion,
                                fecha = fecha
                            )
                        )

                        // Actualizar ViewModel y guardar
                        viewModel.setNombre(nombre)
                        viewModel.setUbicacion(ubicacion)
                        viewModel.setCapacidad(capacidad)
                        viewModel.setTelefono(telefono)
                        viewModel.setCorreo(correo)
                        viewModel.setDescripcion(descripcion)
                        viewModel.setFecha(fecha)
                        viewModel.create()

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
        }
    }
}


