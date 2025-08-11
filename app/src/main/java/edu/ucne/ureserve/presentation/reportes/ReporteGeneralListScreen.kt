package edu.ucne.ureserve.presentation.reportes

import ReservationDetailBlock
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
fun ReporteGeneralListScreen(
    navController: NavController,
    viewModel: ReporteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadReservas()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                    painter = painterResource(id = R.drawable.icon_adminsettings),
                    contentDescription = "Configuración Empleado",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2E5C94))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_table),
                            contentDescription = "General",
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            text = "REPORTE GENERAL",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else if (state.error != null) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
        } else if (state.reservas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay reservas",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            items(state.reservas) { reserva ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, Color(0xFF133986), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                        .background(Color(0xFF2E5C94))
                ) {
                    ReservationDetailBlock("NO. RESERVA", reserva.codigoReserva.toString())
                    ReservationDetailBlock("FECHA", reserva.fechaFormateada)
                    ReservationDetailBlock("HORARIO", "${reserva.horaInicio} a ${reserva.horaFin}")
                    ReservationDetailBlock("MATRÍCULA", reserva.matricula)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("DESCARGAR", Color(0xFF007ACC)) {
                    if (state.reservas.isNotEmpty()) {
                        val pdfFile = generarPdfReservasGeneral(context, state.reservas)
                        Toast.makeText(
                            context,
                            "PDF guardado en: ${pdfFile.absolutePath}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "No hay reservas para generar PDF",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ActionButton("IMPRIMIR", Color(0xFF00B4D8)) {
                    if (state.reservas.isNotEmpty()) {
                        imprimirPdfGeneral(context, state.reservas)
                    } else {
                        Toast.makeText(
                            context,
                            "No hay reservas para imprimir",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "VOLVER",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp
                )
            }
        }
    }
}