package edu.ucne.ureserve.presentation.cubiculos

import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCubiculoScreen(
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    cubiculoId: Int? = null,
    navController: NavController,
    usuarioDTO: UsuarioDTO
) {
    LaunchedEffect(usuarioDTO.usuarioId) {
        viewModel.resetGroupMembers()
        viewModel.initializeWithUser(usuarioDTO)
    }

    val hours by viewModel.selectedHours.collectAsState()
    val members by viewModel.groupMembers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_reserve),
                        contentDescription = "Logo",
                        modifier = Modifier.size(60.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.icon_cubiculo),
                        contentDescription = "Icono de Cubículo",
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6D87A4)
            )
        )

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
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
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Añade los integrantes de tu grupo",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = { /* Add member */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_agregarcubicul),
                        contentDescription = "Agregar",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F4C81), RoundedCornerShape(4.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F4C81))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nombre",
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow,
                        modifier = Modifier.weight(1f).padding(start = 16.dp)
                    )
                    Text(
                        text = "Matrícula",
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow,
                        modifier = Modifier.weight(1f).padding(end = 16.dp),
                        textAlign = TextAlign.End
                    )
                }

                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

                // Mostrar los miembros del grupo (incluyendo al usuario principal ya agregado)
                members.forEachIndexed { i, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (i % 2 == 0) Color(0xFFE6E6E6) else Color.White)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${member.nombres} ${member.apellidos}",
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = member.estudiante?.matricula ?: "No disponible",
                            modifier = Modifier.weight(1f).padding(end = 16.dp),
                            textAlign = TextAlign.End,
                            color = Color.Black
                        )
                    }
                    if (i < members.size - 1) {
                        Divider(color = Color.White, thickness = 1.dp)
                    }
                }
            }

            Text(
                text = "Debe tener mínimo 3 miembros.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Cancelar reserva */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Text(text = "CANCELAR")
            }
            Button(
                onClick = { /* Continuar reserva */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "SIGUIENTE")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E5C94))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* onBottomNavClick("Inicio") */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_inicio),
                    contentDescription = "Inicio",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservaCubiculoScreen() {
    MaterialTheme {
        ReservaCubiculoScreen(
            navController = rememberNavController(),
            usuarioDTO = UsuarioDTO(
                usuarioId = 1,
                nombres = "Juan",
                apellidos = "Perez",
                estudiante = EstudianteDto(matricula = "2022-0465")
            )
        )
    }
}