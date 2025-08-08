package edu.ucne.ureserve.presentation.empleados

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.ureserve.R

@Composable
fun RestauranteSwitchScreen(
    navController: NavController,
    viewModel: EmpleadoViewModel = hiltViewModel()
){
    val restaurantes by viewModel.restaurantes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarRestaurantes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
    ) {
        Column {
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
                    painter = painterResource(id = R.drawable.icon_restaurante),
                    contentDescription = "Icono Restaurante",
                    modifier = Modifier.size(50.dp)
                )
            }

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
                        text = "Restaurantes Disponibles",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        fontSize = 20.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF004BBB), shape = RoundedCornerShape(8.dp))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (error != null) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(restaurantes) { restaurante ->
                            RestauranteItem(
                                restauranteId = restaurante.restauranteId,
                                nombre = restaurante.nombre,
                                ubicacion = restaurante.ubicacion,
                                capacidad = restaurante.capacidad,
                                telefono = restaurante.telefono,
                                correo = restaurante.correo,
                                descripcion = restaurante.descripcion,
                                disponible = restaurante.disponible,
                            ) {
                                viewModel.actualizarDisponibilidadRestaurantes(restaurante.restauranteId, it)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6895D2))
            ) {
                Text(
                    text = "VOLVER",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RestauranteItem(
    restauranteId: Int?,
    nombre: String,
    ubicacion: String,
    capacidad: Int,
    telefono: String,
    correo: String,
    descripcion: String,
    disponible: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var currentState by remember(disponible) { mutableStateOf(disponible) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val iconRes = when (nombre) {
        "SalaVIP" -> R.drawable.sala
        "SalaReuniones" -> R.drawable.salon
        else -> R.drawable.comer
    }

    val iconBackgroundColor = when (nombre) {
        "SalaVIP" -> Color(0xFFFFD500)
        "SalaReuniones" -> Color(0xFFFFD500)
        else -> Color(0xFFFFD500)
    }

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
                    .background(iconBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .size(60.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "$nombre Icon",
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = nombre,
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
                "Error al actualizar el estado",
                Toast.LENGTH_SHORT
            ).show()
            showError = false
        }
    }
}