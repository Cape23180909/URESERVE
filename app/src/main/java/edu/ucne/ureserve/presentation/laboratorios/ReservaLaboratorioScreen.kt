package edu.ucne.ureserve.presentation.laboratorios

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaLaboratorioScreen(
    viewModel: ReservaLaboratorioViewModel = hiltViewModel(),
    laboratorioId: Int? = null,
    navController: NavController,
    usuarioDTO: UsuarioDTO,
    estudiante: EstudianteDto,
    horaInicio: String,
    horaFin: String
){

    val hours by viewModel.selectedHours.collectAsState()
    val allMembers by viewModel.members.collectAsState()
    val laboratorioNombre by viewModel.laboratorioNombre.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var validarCantidaHoras by remember { mutableStateOf(false) }

    LaunchedEffect(allMembers) {
        Log.d("ReservaLaboratorioScreen", "Miembros actualizados: ${allMembers.size}")
        allMembers.forEach { member ->
            Log.d("ReservaLaboratorioScreen", "Miembro: ${member.nombres}")
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ReservaScreen", "Usuario recibido - Nombre: ${usuarioDTO.nombres}, Matrícula: ${usuarioDTO.estudiante?.matricula ?: "N/A"}")
    }

    LaunchedEffect(usuarioDTO) {
        viewModel.initializeWithUser(usuarioDTO)
    }

    LaunchedEffect(laboratorioId) {
        laboratorioId?.let { viewModel.getLaboratorioNombreById(it) }
    }

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
                    Image(painter = painterResource(id = R.drawable.logo_reserve), contentDescription = "Logo", modifier = Modifier.size(60.dp))
                    Image(painter = painterResource(id = R.drawable.icon_laboratorio), contentDescription = "Icono de laboratorio", modifier = Modifier.size(60.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
        )

        Text(
            text = "Reserva de $laboratorioNombre",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                "Seleccione la cantidad de horas:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Digite la cantidad de horas:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = hours,
                onValueChange = {
                    if (it.isEmpty() || it.all(Char::isDigit)) {
                        viewModel.setSelectedHours(it)
                        validarCantidaHoras = false // Limpiamos el error al ingresar
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = validarCantidaHoras
            )

            if (validarCantidaHoras) {
                Text(
                    text = "Debe digitar la hora antes de agregar Integrantes.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
                        if (hours.isBlank()) {
                            validarCantidaHoras  = true
                        } else {
                            validarCantidaHoras  = false
                            navController.navigate("AgregarEstudiante") {
                                launchSingleTop = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_agregarcubicul),
                        contentDescription = "Agregar",
                        modifier = Modifier.size(46.dp)
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
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nombre", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(start = 16.dp))
                    Text("Matrícula", fontWeight = FontWeight.Bold, color = Color.Yellow, modifier = Modifier.weight(1f).padding(end = 16.dp), textAlign = TextAlign.End)
                }
                Divider(color = Color.White, thickness = 1.dp)
                allMembers.forEachIndexed { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.White else Color.LightGray)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = member.nombres.orEmpty() + " " + member.apellidos.orEmpty(),
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = member.estudiante?.matricula.orEmpty(),
                            modifier = Modifier.weight(1f).padding(end = 16.dp),
                            textAlign = TextAlign.End,
                            color = Color.Black
                        )
                    }
                    if (index < allMembers.size - 1) {
                        Divider(color = Color.White, thickness = 1.dp)
                    }
                }
            }

            val faltantes = (3 - allMembers.size).coerceAtLeast(0)
            Text(
                text = if (faltantes > 0)
                    "Faltan $faltantes ${if (faltantes == 1) "miembro" else "miembros"} para completar el mínimo requerido (3)."
                else
                    "Tienes el mínimo requerido (3 miembros).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF004BBB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "VOLVER",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    if (allMembers.size >= 3 && hours.isNotBlank()) {
                        try {
                            val cantidadHoras = hours.toInt()
                            val matricula = usuarioDTO.estudiante?.matricula ?: ""

                            viewModel.confirmarReservaLaboratorio(
                                cubiculoId = laboratorioId ?: 0,
                                cantidadHoras = cantidadHoras,
                                matricula = matricula,
                                onSuccess = { codigo ->
                                    navController.navigate("ReservaLaboratorioExitosa/$codigo") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onError = { mensaje ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(mensaje)
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error al procesar reserva: ${e.message}")
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Debe tener mínimo 3 miembros y horas válidas.")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6895D2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SIGUIENTE",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF2E5C94)).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /* TODO: Ir a Inicio */ }) {
                Icon(painter = painterResource(id = R.drawable.icon_inicio), contentDescription = "Inicio", tint = Color.White)
            }
        }
    }
}