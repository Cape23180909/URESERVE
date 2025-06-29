package edu.ucne.ureserve.presentation.restaurantes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.ureserve.R

@Composable
fun RegistroReservaRestauranteScreen(
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
        // Header
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
                text = "Registro Restaurante",
                color = Color(0xFF023E8A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.comer),
                contentDescription = "Restaurante",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Formulario para $fecha",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RegistroReservaRestauranteForm(
            onCancelarClick = onCancelarClick,
            onConfirmarClick = { correo, nombres, apellidos, celular, matricula, cedula, direccion ->
                DatosPersonalesRestauranteStore.lista.add(
                    DatosPersonalesRestaurante(
                        correo = correo,
                        nombres = nombres,
                        apellidos = apellidos,
                        celular = celular,
                        matricula = matricula,
                        cedula = cedula,
                        direccion = direccion
                    )
                )
                // Configurar datos de reserva en el ViewModel
                viewModel.setFecha(fecha)
                viewModel.setHorario("12:00 PM") // Puedes usar un selector si deseas
                viewModel.setCantidad(1)         // Aquí puedes ajustar según tus datos
                viewModel.setEstado(1)
                viewModel.setCodigoReserva(matricula.hashCode())

                // Guardar en el backend
                viewModel.create()

                onConfirmarClick()
            }
        )
    }
}

@Composable
private fun RegistroReservaRestauranteForm(
    onCancelarClick: () -> Unit,
    onConfirmarClick: (
        correo: String,
        nombres: String,
        apellidos: String,
        celular: String,
        matricula: String,
        cedula: String,
        direccion: String
    ) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val formularioCompleto = listOf(
        correo, nombres, apellidos, celular, matricula, cedula, direccion
    ).all { it.isNotBlank() }

    val campos = listOf(
        Triple("Correo electrónico *", correo) { v: String -> correo = v },
        Triple("Nombres *", nombres) { v: String -> nombres = v },
        Triple("Apellidos *", apellidos) { v: String -> apellidos = v },
        Triple("Número de celular *", celular) { v: String -> celular = v },
        Triple("Matrícula *", matricula) { v: String -> matricula = v },
        Triple("Cédula *", cedula) { v: String -> cedula = v },
        Triple("Dirección *", direccion) { v: String -> direccion = v }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Datos Personales",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF023E8A)
        )
        Spacer(Modifier.height(12.dp))

        campos.forEach { (label, value, onValueChange) ->
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                        correo, nombres, apellidos,
                        celular, matricula, cedula, direccion
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistroReservaRestaurante() {
    MaterialTheme {
        RegistroReservaRestauranteScreen(
            fecha = "20/06/2025",
            onCancelarClick = {},
            onConfirmarClick = {}
        )
    }
}
