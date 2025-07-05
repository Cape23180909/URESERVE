package edu.ucne.ureserve.presentation.cubiculos

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCubiculoScreen(
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    cubiculoId: Int? = null,
    navController: NavController,
    usuarioDTO: UsuarioDTO,
    estudiante: EstudianteDto,
) {
    // Debug inicial
    LaunchedEffect(Unit) {
        Log.d("ReservaScreen", "Usuario recibido - Nombre: ${usuarioDTO.nombres}, Matrícula: ${usuarioDTO.estudiante?.matricula ?: "N/A"}")
    }

    // Inicialización única con usuario
    LaunchedEffect(usuarioDTO) {
        Log.d("ReservaScreen", "Inicializando ViewModel con usuario...")
        viewModel.initializeWithUser(usuarioDTO)
    }

    val members by remember { derivedStateOf { viewModel.members } }
    val hours by viewModel.selectedHours.collectAsState()

    var localMembers by remember { mutableStateOf(listOf(usuarioDTO)) }

    // Combinar con miembros del ViewModel
    val allMembers = remember(localMembers, viewModel.members) {
        (localMembers + viewModel.members).distinctBy { it.usuarioId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
    ) {
        // Top AppBar
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

        // Título
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

        // Campo para horas
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Miembros del grupo
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
                IconButton(onClick = {
                    if (members.none { it.usuarioId == usuarioDTO.usuarioId }) {
                        viewModel.addMember(usuarioDTO)
                    }
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_agregarcubicul),
                        contentDescription = "Agregar",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F4C81), RoundedCornerShape(4.dp))
            ) {
                // Encabezado
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F4C81))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nombre", color = Color.Yellow, fontWeight = FontWeight.Bold)
                        Text("Matrícula", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }
                    Divider(color = Color.White)
                }

                // Si no hay miembros
                if (members.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("No hay miembros agregados", color = Color.Black)
                        }
                        Divider(color = Color.White)
                    }
                } else {
                    // Si hay miembros
                    items(members) { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (members.indexOf(member) % 2 == 0)
                                        Color.LightGray
                                    else
                                        Color.White
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${usuarioDTO.nombres}",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(member.estudiante?.matricula ?: "Sin matrícula", color = Color.Black)
                        }
                        Divider(color = Color.White)
                    }
                }

                // Footer con mensaje dinámico
                item {
                    val faltantes = (3 - members.size).coerceAtLeast(0)
                    Text(
                        text = if (faltantes > 0)
                            "Debe tener mínimo $faltantes miembros más"
                        else
                            "Tienes el mínimo requerido",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones inferiores
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

        // Barra de navegación inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E5C94))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* navController.navigate("Inicio") */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_inicio),
                    contentDescription = "Inicio",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MemberItem(member: UsuarioDTO) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${member.nombres} ${member.apellidos}",
            color = Color.Black
        )
        Text(
            text = member.estudiante?.matricula ?: "Sin matrícula",
            color = Color.Black
        )
    }
    Divider(color = Color.White)
}