import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlanificadorLaboratorioScreen(
    navController: NavController,
    laboratorioId: Int?,
    laboratorioNombre: String,
    fechaSeleccionada: Calendar
) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }
    val horariosDisponibles = listOf(
        "07:00 AM", "07:30 AM", "08:00 AM", "08:30 AM",
        "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
        "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
        "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM",
        "06:00 PM", "06:30 PM", "07:00 PM", "08:00 PM", "08:30 PM", "09:00 PM", "09:30 PM", "10:00 PM", "10:30 PM",
        "11:00 PM", "11:30 PM", "12:00 AM",
    )

    var horaInicioSeleccionada by remember { mutableStateOf("") }
    var horaFinSeleccionada by remember { mutableStateOf("") }
    var mostrarSeleccionFin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Obtener la hora actual y configurar la hora de inicio y fin
    LaunchedEffect(Unit) {
        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
        horaInicioSeleccionada = horaActual.format(formatter)

        val horaActualIndex = horariosDisponibles.indexOfFirst {
            LocalTime.parse(it, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)) >= horaActual
        }
        horaFinSeleccionada = if (horaActualIndex != -1 && horaActualIndex < horariosDisponibles.lastIndex) {
            horariosDisponibles[horaActualIndex + 1]
        } else {
            horariosDisponibles.last()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF023E8A))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_reserve),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "PLANIFICADORA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = laboratorioNombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .border(2.dp, Color.Green, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DISPONIBLE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }

        Text(
            text = "Fecha seleccionada: ${formatoFecha(fechaSeleccionada)}",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(horariosDisponibles) { horario ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = horario,
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccione el horario:",
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Desde:", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = horaInicioSeleccionada, fontSize = 14.sp, color = Color.Black)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Hasta:", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { mostrarSeleccionFin = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = horaFinSeleccionada, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        if (mostrarSeleccionFin) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                items(
                    horariosDisponibles.filter {
                        LocalTime.parse(it, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)) >
                                LocalTime.parse(horaInicioSeleccionada, DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
                    }
                ) { hora ->
                    Text(
                        text = hora,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                horaFinSeleccionada = hora
                                mostrarSeleccionFin = false
                            }
                            .padding(8.dp),
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF004BBB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CANCELAR", fontSize = 14.sp)
            }
            Button(
                onClick = {
                    val fechaMillis = fechaSeleccionada.timeInMillis
                    notificationHandler.showNotification(
                        title = "Confirmación",
                        message = "Horario confirmado: $horaInicioSeleccionada - $horaFinSeleccionada"
                    )
                    val route = "reservaLaboratorio/$laboratorioId/$horaInicioSeleccionada/$horaFinSeleccionada/$fechaMillis"
                    navController.navigate(route)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6895D2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CONFIRMAR", fontSize = 14.sp)
            }
        }
    }
}

fun formatoFecha(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}