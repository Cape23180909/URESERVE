package edu.ucne.ureserve.presentation.restaurantes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminosReservaVipScreen(
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
                            text = "Términos de Reserva - VIP",
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

                Text(
                    text = "Términos de reservas de la sala VIP",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    TerminoItem("Las reservas las puede hacer cualquier tipo de persona, sin importar que sea estudiante o no.")
                    TerminoItem("El costo es de \$15,000. Las reservaciones se pueden agendar hasta con un día de antelación e incluso hasta con un año de anticipación.")
                    TerminoItem("Se pueden hacer siempre y cuando tengan el espacio disponible. Las reservas duran un día completo.")
                    TerminoItem("El salón VIP cuenta con proyectores, pantalla de proyección, música, asientos y mesas.")
                    TerminoItem("Este apartado cuenta con una capacidad máxima de 15 personas.")
                    TerminoItem("Si desea incluir refrigerios, estos serán cargados a la cotización del salón.")
                    TerminoItem("Los refrigerios son preparados en la UCNE. También puede contar con meseros, pero aumenta el costo.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = aceptado,
                        onCheckedChange = { aceptado = it },
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

                Text(
                    text = "Gracias por utilizar UReserve!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                        onClick = {
                            if (aceptado) onAceptarClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                        enabled = aceptado
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
fun TerminoItem(
    text: String,
    bulletColor: Color = Color(0xFF023E8A),
    textColor: Color = Color.Black,
    bulletSize: TextUnit = 20.sp,
    textSize: TextUnit = 16.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            modifier = Modifier.padding(end = 8.dp),
            fontSize = bulletSize,
            color = bulletColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = textSize,
            color = textColor,
            lineHeight = textSize.times(1.25f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTerminosReservaVipScreen() {
    TerminosReservaVipScreen()
}
