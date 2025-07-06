package edu.ucne.ureserve.presentation.cubiculos

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavGraph.Companion.findStartDestination
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
        viewModel.initializeWithUser(usuarioDTO)
    }

    val hours by viewModel.selectedHours.collectAsState()
    val allMembers by viewModel.members.collectAsState()

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
                    Image(painter = painterResource(id = R.drawable.logo_reserve), contentDescription = "Logo", modifier = Modifier.size(60.dp))
                    Image(painter = painterResource(id = R.drawable.icon_cubiculo), contentDescription = "Icono de Cubículo", modifier = Modifier.size(60.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
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

        // Horas
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text("Seleccione la cantidad de horas:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Digite la cantidad de horas:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = hours,
                onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) viewModel.setSelectedHours(it) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
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

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Añade los integrantes de tu grupo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(
                    onClick = {
                        navController.navigate("AgregarEstudiante") {
                            launchSingleTop = true
                            // Esto asegura que al volver mantenga el estado
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_agregarcubicul),
                        contentDescription = "Agregar"
                    )
                }
            }

            // Lista con filas fijas
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
                    Text("Nombre", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(start = 16.dp))
                    Text("Matrícula", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(end = 16.dp), textAlign = TextAlign.End)
                }

                Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

                for (i in 0 until 6) {
                    val member = allMembers.getOrNull(i)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (i % 2 == 0) Color.White else Color.LightGray)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = member?.nombres?.takeIf { it.isNotBlank() }?.plus(" ${member.apellidos}") ?: "",
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = member?.estudiante?.matricula ?: "",
                            modifier = Modifier.weight(1f).padding(end = 16.dp),
                            textAlign = TextAlign.End,
                            color = Color.Black
                        )
                    }
                    if (i < 5) Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                }
            }

            val faltantes = (3 - allMembers.size).coerceAtLeast(0)
            Text(
                text = if (faltantes > 0) "Debe tener mínimo $faltantes miembros más" else "Tienes el mínimo requerido",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones inferiores
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))) {
                Text(text = "CANCELAR")
            }
            Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text(text = "SIGUIENTE")
            }
        }

        // Navegación inferior
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF2E5C94)).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* navController.navigate("Inicio") */ }) {
                Icon(painter = painterResource(id = R.drawable.icon_inicio), contentDescription = "Inicio", tint = Color.White)
            }
        }
    }
}