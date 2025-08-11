package edu.ucne.ureserve.presentation.reportes

import ReservationDetailBlock
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun ReporteProyectoresScreen(
    navController: NavController,
    viewModel: ReporteViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.loadReservasPorTipo(1)
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3278))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
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

            Spacer(modifier = Modifier.height(16.dp))

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
                            painter = painterResource(id = R.drawable.icon_proyector),
                            contentDescription = "Proyector",
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            text = "REPORTE DE PROYECTORES",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(color = Color.White)
                        }
                        state.error != null -> {
                            Text(
                                text = state.error,
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                        state.reservas.isEmpty() -> {
                            Text(
                                text = "No hay reservas de proyectores",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reservas.forEach { reserva ->
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
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("DESCARGAR", Color(0xFF007ACC)) {
                    if (state.reservas.isNotEmpty()) {
                        val pdfFile = generarPdfReservasProyectores(context, state.reservas)
                        Toast.makeText(context, "PDF guardado en: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "No hay reservas para generar PDF", Toast.LENGTH_SHORT).show()
                    }
                }

                ActionButton("IMPRIMIR", Color(0xFF00B4D8)) {
                    if (state.reservas.isNotEmpty()) {
                        imprimirPdfProyectores(context, state.reservas)
                    } else {
                        Toast.makeText(context, "No hay reservas para imprimir", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
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