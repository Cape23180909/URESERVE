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
import androidx.navigation.NavController
import edu.ucne.ureserve.R

@Composable
fun ReservaSalaVipScreen(
    fecha: String,
    onCancelarClick: () -> Unit = {},
    navController: NavController,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correoElectronico by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        nombres, apellidos, matricula, cedula, telefono, correoElectronico, direccion
    ).all { it.isNotBlank() }

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
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = { Text("Correo electrónico *") },
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
                onValueChange = { apellidos = it },
                label = { Text("Apellidos *") },
                modifier = Modifier.fillMaxWidth()
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
                value = buildString {
                    append(matricula.take(8)) // Tomar máximo 8 dígitos
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
                    // Formatear automáticamente mientras se escribe
                    when {
                        length <= 3 -> this
                        length <= 10 -> "${substring(0, 3)}-${substring(3)}"
                        else -> "${substring(0, 3)}-${substring(3, 10)}-${substring(10)}"
                    }
                },
                onValueChange = { newValue ->
                    // Eliminar guiones existentes para el procesamiento
                    val cleanValue = newValue.filter { it.isDigit() }
                    // Limitar a 11 dígitos (3 + 7 + 1)
                    cedula = cleanValue.take(11)
                },
                label = { Text("Cédula *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("XXX-XXXXXXX-X") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        nombres = ""
                        apellidos = ""
                        cedula = ""
                        matricula = ""
                        telefono = ""
                        correoElectronico = ""
                        direccion = ""
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
                        DatosPersonalesSalaVipStore.lista.add(
                            DatosPersonalesSalaVip(
                                nombre = nombres,
                                apellidos = apellidos,
                                cedula = cedula,
                                matricula = matricula,
                                telefono = telefono,
                                correoElectronico = correoElectronico,
                                direccion = direccion,
                                fecha = fecha
                            )
                        )

                        viewModel.setNombres(nombres)
                        viewModel.setApellidos(apellidos)
                        viewModel.setCedula(cedula)
                        viewModel.setMatricula(matricula)
                        viewModel.setTelefono(telefono)
                        viewModel.setCorreo(correoElectronico)
                        viewModel.setDireccion(direccion)
                        viewModel.setFecha(fecha)

                        navController.navigate("PagoSalaVipScreen?fecha=$fecha")
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