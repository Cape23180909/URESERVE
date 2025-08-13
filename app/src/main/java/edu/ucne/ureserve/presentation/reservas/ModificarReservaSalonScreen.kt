package edu.ucne.ureserve.presentation.reservas

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.restaurantes.RestaurantesViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ModificarReservaSalonScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }

    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(postNotificationPermission) {
        if (postNotificationPermission?.status?.isGranted == false) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.NOON) }
    var endTime by remember { mutableStateOf(LocalTime.NOON.plusHours(1)) }
    var originalDate by remember { mutableStateOf<LocalDate?>(null) }
    var originalStartTime by remember { mutableStateOf<LocalTime?>(null) }
    var originalEndTime by remember { mutableStateOf<LocalTime?>(null) }

    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                viewModel.cargarReservaParaModificar(id)
                viewModel.reservaSeleccionada.value?.let { reserva ->
                    val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                    val horaInicio = LocalTime.parse(reserva.horaInicio)
                    val horaFin = LocalTime.parse(reserva.horaFin)

                    selectedDate = fecha
                    startTime = horaInicio
                    endTime = horaFin

                    originalDate = fecha
                    originalStartTime = horaInicio
                    originalEndTime = horaFin
                }
            } catch (e: Exception) {
                errorMessage = "Error al cargar la reserva: ${e.message}"
            }
        }
    }

    val hasChanges = remember(selectedDate, startTime, endTime, originalDate, originalStartTime, originalEndTime) {
        derivedStateOf {
            selectedDate != originalDate ||
                    startTime != originalStartTime ||
                    endTime != originalEndTime
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ModificarReservaTopBar(onBackClick = { navController?.popBackStack() })
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
                text = "MODIFICAR SALÓN DE REUNIÓN",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ErrorMessage(text = errorMessage)
            ErrorMessage(text = uiState.errorMessage)

            CurrentReservationCard(selectedDate = selectedDate)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cambiar Fecha", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SaveChangesButton(
                enabled = hasChanges.value && !uiState.isLoading,
                isLoading = uiState.isLoading,
                onSaveClick = {
                    reservaId?.let { id ->
                        viewModel.modificarReservaRestauranteCompleta(
                            nuevaFecha = selectedDate,
                            nuevaHoraInicio = startTime,
                            nuevaHoraFin = endTime,
                            onSuccess = {
                                notificationHandler.showNotification(
                                    title = "Cambios guardados",
                                    message = "Tu reserva ha sido actualizada."
                                )
                                navController?.navigate("reservaList") {
                                    popUpTo("modificar_salon/$id") { inclusive = true }
                                }
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController?.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("REGRESAR", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (showDatePicker) {
            DatePickerDialogComponent(
                initialDate = selectedDate,
                onDateSelected = { selectedDate = it },
                onDismiss = { showDatePicker = false },
                notificationHandler = notificationHandler
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModificarReservaTopBar(onBackClick: () -> Unit) {
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
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6D87A4))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF023E8A))
        )
    }
}

@Composable
private fun ErrorMessage(text: String?) {
    if (!text.isNullOrEmpty()) {
        Text(text = text, color = Color.Red, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CurrentReservationCard(selectedDate: LocalDate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reserva actual:", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                color = Color.White
            )
        }
    }
}

@Composable
private fun SaveChangesButton(
    enabled: Boolean,
    isLoading: Boolean,
    onSaveClick: () -> Unit
) {
    Button(
        onClick = onSaveClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF0077B6) else Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
        } else {
            Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogComponent(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    notificationHandler: NotificationHandler
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !date.isBefore(LocalDate.now())
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(date)
                }
                notificationHandler.showNotification(
                    title = "Fecha modificada",
                    message = "La nueva fecha fue seleccionada correctamente."
                )
                onDismiss()
            }) {
                Text("CONFIRMAR")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    "Seleccione nueva fecha",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}