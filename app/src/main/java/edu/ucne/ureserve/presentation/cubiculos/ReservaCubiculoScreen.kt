package edu.ucne.ureserve.presentation.cubiculos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoViewModel.Member


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCubiculoScreen(
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    cubiculoId: Int? = null,
    navController: NavController
) {
    val hours by viewModel.selectedHours.collectAsState()
    val members by viewModel.groupMembers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        // Cambiado: Título centrado sin TopAppBar
        Text(
            text = "Reserva de cubículo #$cubiculoId",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sección de horas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Cambiado: Dos líneas de texto como en la imagen
            Text(
                text = "Seleccione la cantidad de horas:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Digite la cantidad de horas:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = hours,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        viewModel.setSelectedHours(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de miembros
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Cambiado: Título con estilo similar a la imagen
            Text(
                text = "Añade los integrantes de tu grupo",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Cambiado: Tabla con encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                // Encabezados de tabla
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nombre", fontWeight = FontWeight.Bold)
                    Text("Matrícula", fontWeight = FontWeight.Bold)
                }

                // Línea divisoria
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

                // Filas de miembros
                // Cambiado: Mostrar siempre 6 filas (con datos o vacías)
                for (i in 0 until 6) {
                    val member = if (i < members.size) members[i] else  Member("", "")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = member.name.ifEmpty { " " },
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = member.id.ifEmpty { " " },
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }

                    if (i < 5) {
                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Cambiado: Nota sobre mínimo de miembros
            Text(
                text = "Debe tener mínimo 3 miembros.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones inferiores
        // Cambiado: Distribución y estilos como en la imagen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Cancelar reserva */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "CANCELAR")
            }
            Button(
                onClick = { /* Continuar reserva */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Text(text = "SIGUIENTE")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaCubiculoScreen() {
    MaterialTheme {
        ReservaCubiculoScreen(navController = rememberNavController())
    }
}