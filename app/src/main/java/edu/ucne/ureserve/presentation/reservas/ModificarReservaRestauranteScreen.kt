package edu.ucne.ureserve.presentation.restaurantes

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ModificarReservaRestauranteScreen(
    reservaId: Int? = null,
    navController: NavHostController? = null,
    viewModel: RestaurantesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else null
    val notificationHandler = remember { NotificationHandler(context) }

    RequestNotificationPermission(postNotificationPermission)

    val uiState by viewModel.uiState.collectAsState()
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.NOON) }
    var endTime by remember { mutableStateOf(LocalTime.NOON.plusHours(1)) }
    var originalDate by remember { mutableStateOf<LocalDate?>(null) }
    var originalStartTime by remember { mutableStateOf<LocalTime?>(null) }
    var originalEndTime by remember { mutableStateOf<LocalTime?>(null) }

    val hasChanges by remember {
        derivedStateOf {
            selectedDate != originalDate ||
                    startTime != originalStartTime ||
                    endTime != originalEndTime
        }
    }

    LoadReservaData(
        reservaId,
        viewModel,
        setErrorMessage,
        onLoad = { fecha, horaInicio, horaFin ->
            selectedDate = fecha
            startTime = horaInicio
            endTime = horaFin
            originalDate = fecha
            originalStartTime = horaInicio
            originalEndTime = horaFin
        }
    )

    Scaffold(
        topBar = {
            AppTopBar(navController)
        },
        containerColor = Color(0xFF023E8A)
    ) { innerPadding ->
        ContentBody(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            errorMessage = errorMessage,
            uiErrorMessage = uiState.errorMessage,
            selectedDate = selectedDate,
            hasChanges = hasChanges,
            isLoading = uiState.isLoading,
            onShowDatePicker = { setShowDatePicker(true) },
            onSaveChanges = {
                notificationHandler.showNotification(
                    title = "Cambios detectados",
                    message = "Los cambios fueron guardados correctamente."
                )
                reservaId?.let { id ->
                    viewModel.modificarReservaRestauranteCompleta(
                        nuevaFecha = selectedDate,
                        nuevaHoraInicio = startTime,
                        nuevaHoraFin = endTime,
                        onSuccess = {
                            navController?.navigate("reservaList") {
                                popUpTo("modificar_restaurante/$id") { inclusive = true }
                            }
                        },
                        onError = { error -> setErrorMessage(error) }
                    )
                }
            },
            onBack = { navController?.popBackStack() }
        )

        if (showDatePicker) {
            DatePickerDialogComponent(
                initialDate = selectedDate,
                onDismiss = { setShowDatePicker(false) },
                onConfirm = { newDate ->
                    selectedDate = newDate
                    notificationHandler.showNotification(
                        title = "Fecha modificada",
                        message = "La nueva fecha fue seleccionada correctamente."
                    )
                    setShowDatePicker(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RequestNotificationPermission(postNotificationPermission: com.google.accompanist.permissions.PermissionState?) {
    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LoadReservaData(
    reservaId: Int?,
    viewModel: RestaurantesViewModel,
    setErrorMessage: (String?) -> Unit,
    onLoad: (LocalDate, LocalTime, LocalTime) -> Unit
) {
    LaunchedEffect(reservaId) {
        reservaId?.let { id ->
            try {
                viewModel.cargarReservaParaModificar(id)
                viewModel.reservaSeleccionada.value?.let { reserva ->
                    val fecha = LocalDate.parse(reserva.fecha.substring(0, 10))
                    val horaInicio = LocalTime.parse(reserva.horaInicio)
                    val horaFin = LocalTime.parse(reserva.horaFin)
                    onLoad(fecha, horaInicio, horaFin)
                }
            } catch (e: Exception) {
                setErrorMessage("Error al cargar la reserva: ${e.message}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(navController: NavHostController?) {
    Column {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_reserve),
                        contentDescription = "Logo",
                        modifier = Modifier.size(50.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.icon_reserva),
                        contentDescription = "Reserva",
                        modifier = Modifier.size(50.dp)
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
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentBody(
    modifier: Modifier,
    errorMessage: String?,
    uiErrorMessage: String?,
    selectedDate: LocalDate,
    hasChanges: Boolean,
    isLoading: Boolean,
    onShowDatePicker: () -> Unit,
    onSaveChanges: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }
        uiErrorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = "MODIFICAR RESERVA DE RESTAURANTE",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Reserva actual:", color = Color.White, fontWeight = FontWeight.Bold)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Text(
                        "Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onShowDatePicker,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cambiar Fecha", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSaveChanges,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasChanges) Color(0xFF0077B6) else Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = hasChanges && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5C94)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("REGRESAR", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogComponent(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
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
                    val selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onConfirm(selectedDate)
                }
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
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewModificarReservaRestauranteScreen() {
    ModificarReservaRestauranteScreen(navController = null)
}