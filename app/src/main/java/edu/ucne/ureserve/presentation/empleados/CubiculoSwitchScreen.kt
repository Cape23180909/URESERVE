package edu.ucne.ureserve.presentation.empleados

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.ureserve.R

@Composable
fun CubiculoSwitchScreen(
    navController: NavController,
    viewModel: EmpleadoViewModel = hiltViewModel()
) {
    val cubiculos by viewModel.cubiculos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarCubiculos()
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp) // Espacio para el botón
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color(0xFFA7A7A7))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_reserve),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.icon_cubiculo),
                    contentDescription = "Icono Cubículo",
                    modifier = Modifier.size(50.dp)
                )
            }

            // Título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFD600), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cubículos Disponibles",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        fontSize = 20.sp
                    )
                }
            }

            // Lista de cubículos o loader
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF004BBB), shape = RoundedCornerShape(8.dp))
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }

                    error != null -> {
                        Text(
                            text = error!!,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            cubiculos.forEach { cubiculo ->
                                CubiculoItem(
                                    cubiculoId = cubiculo.cubiculoId,
                                    nombre = cubiculo.nombre,
                                    disponible = cubiculo.disponible
                                ) { nuevoEstado ->
                                    viewModel.actualizarDisponibilidadCubiculos(
                                        cubiculo.cubiculoId,
                                        nuevoEstado
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Botón volver al final
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6895D2))
            ) {
                Text(text = "VOLVER", color = Color.White)
            }
        }
    }
}

@Composable
fun CubiculoItem(
    cubiculoId: Int,
    nombre: String,
    disponible: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var currentState by remember(disponible) { mutableStateOf(disponible) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(disponible) {
        currentState = disponible
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .background(Color(0xFFFFD500), shape = RoundedCornerShape(8.dp))
                    .size(60.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_cubiculo),
                    contentDescription = "Cubículo Icon",
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = nombre.ifBlank { "Cubículo #$cubiculoId" },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(2.dp, Color.Black, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .size(60.dp, 30.dp)
        ) {
            Switch(
                checked = currentState,
                onCheckedChange = { newState ->
                    currentState = newState
                    try {
                        onCheckedChange(newState)
                    } catch (e: Exception) {
                        currentState = !newState
                        showError = true
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color(0xFFFFD500),
                    uncheckedThumbColor = Color.Black,
                    uncheckedTrackColor = Color.White
                )
            )
        }
    }

    if (showError) {
        LaunchedEffect(showError) {
            Toast.makeText(
                context,
                "Error al actualizar el estado del cubículo",
                Toast.LENGTH_SHORT
            ).show()
            showError = false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCubiculoSwitchScreen() {
    CubiculoSwitchScreen(
        navController = rememberNavController()
    )
}