package edu.ucne.ureserve.presentation.restaurantes


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminosReservaScreen(
    onAceptarClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var aceptado by remember { mutableStateOf(false) } // Empieza en false para obligar a marcar checkbox
    Scaffold(
        topBar = {
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
                        Text(
                            text = "Términos de Reserva",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF023E8A)),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.atras),
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = "Términos de reservas del salón de reuniones",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Términos y condiciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    TerminoItem(text = "Las reservas las puede hacer cualquier tipo de persona, sin importar que sea estudiante o no.")
                    TerminoItem(text = "El costo es de \$15,000. Las reservaciones se pueden agendar hasta con un día de antelación e incluso hasta con un año de antelación.")
                    TerminoItem(text = "Se pueden hacer siempre y cuando tengan el espacio disponible. Las reservas duran un día completo.")
                    TerminoItem(text = "El salón de reuniones cuenta con proyectores, pantalla de proyección, música, asientos y mesas.")
                    TerminoItem(text = "Este apartado cuenta con una capacidad máxima de 15 personas.")
                    TerminoItem(text = "Si desea incluir refrigerios, estos serán cargados a la cotización del salón.")
                    TerminoItem(text = "Los refrigerios son preparados en la UCNE. También puede contar con meseros, pero aumenta el costo.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox de aceptación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF023E8A),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estoy de acuerdo con estos términos",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje final
                Text(
                    text = "Gracias por utilizar | Reserve!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onCancelarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text(text = "RECHAZAR", color = Color.White)
                    }

                    Button(
                        onClick = onAceptarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Text(text = "ACEPTAR", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}

@Composable
fun TerminoItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            modifier = Modifier.padding(top = 4.dp, end = 8.dp),
            fontSize = 16.sp,
            color = Color(0xFF023E8A)
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTerminosReservaScreen() {
    TerminosReservaScreen()
}