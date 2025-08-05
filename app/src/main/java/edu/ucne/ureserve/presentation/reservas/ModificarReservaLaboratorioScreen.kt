package edu.ucne.ureserve.presentation.laboratorios

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.navigation.NavHostController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.presentation.login.AuthManager
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarReservaLaboratorioScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: ReservaLaboratorioViewModel = hiltViewModel()
) {
    val members by viewModel.members.collectAsState()
    val laboratorioNombre by viewModel.laboratorioNombre.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val reserva by viewModel.reservaSeleccionada.collectAsState()
    val laboratorioId by viewModel.laboratorioSeleccionado.collectAsState()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var matriculaInput by remember { mutableStateOf("") }

    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            viewModel.getLaboratorioNombreById(id)
        }
    }

    Scaffold(
        topBar = {
            Column {
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
                                painter = painterResource(id = R.drawable.icon_reserva),
                                contentDescription = "Reserva",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6D87A4)
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF023E8A))
                )
            }
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MODIFICAR LABORATORIO",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Información de la reserva:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Integrantes: ${members.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(members) { member ->
                    MemberItem(
                        member = member,
                        onRemove = { matricula ->
                            viewModel.eliminarMiembroPorMatricula(matricula)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showAddMemberDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Agregar Integrante", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    reservaId?.let { id ->
                        try {
                            val fecha = reserva?.fecha?.substring(0, 10) ?: return@let
                            viewModel.modificarReservaLaboratorio(
                                reservaId = id,
                                laboratorioId = laboratorioId ?: 0,
                                fechaLocal = LocalDate.parse(fecha),
                                horaInicio = LocalTime.parse(reserva?.horaInicio ?: "08:00:00"),
                                horaFin = LocalTime.parse(reserva?.horaFin ?: "09:00:00"),
                                matricula = AuthManager.currentUser?.estudiante?.matricula ?: ""
                            )

                            // Navegar a la pantalla de lista de reservas
                            navController?.navigate("reservaList") {
                                popUpTo("modificar_laboratorio") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            viewModel.setError("Error al procesar fechas: ${e.message}")
                        }
                    }
                },
                enabled = members.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0077B6)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController?.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CANCELAR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddMemberDialog = false
                    matriculaInput = ""
                    viewModel.clearError()
                },
                title = { Text("Agregar Integrante") },
                text = {
                    Column {
                        TextField(
                            value = matriculaInput,
                            onValueChange = {
                                matriculaInput = it
                                viewModel.clearError()
                            },
                            label = { Text("Matrícula del estudiante") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        errorMessage?.takeIf { it.contains("matrícula", true) }?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (matriculaInput.isBlank()) {
                                viewModel.setError("La matrícula no puede estar vacía")
                                return@Button
                            }
                            viewModel.buscarUsuarioPorMatricula(matriculaInput) { usuario ->
                                if (usuario != null) {
                                    showAddMemberDialog = false
                                    matriculaInput = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0096C7)
                        ),
                        enabled = matriculaInput.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Agregar")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddMemberDialog = false
                            matriculaInput = ""
                            viewModel.clearError()
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun MemberItem(
    member: UsuarioDTO,
    onRemove: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${member.nombres} ${member.apellidos}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = member.estudiante?.matricula ?: "Sin matrícula",
                    color = Color.White
                )
            }

            IconButton(
                onClick = { member.estudiante?.matricula?.let { onRemove(it) } }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewModificarReservaLaboratorioScreen() {
    ModificarReservaLaboratorioScreen(navController = null)
}