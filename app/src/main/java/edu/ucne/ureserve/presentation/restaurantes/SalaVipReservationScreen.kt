package edu.ucne.ureserve.presentation.salavip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaVipReservationScreen(
    onBottomNavClick: (String) -> Unit = {},
    navController: NavController? = null
) {
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }

    val isSunday = selectedDate?.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    val isDateValid = selectedDate != null && !isSunday

    Column(modifier = Modifier.fillMaxSize()) {
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
                            painter = painterResource(id = R.drawable.sala),
                            contentDescription = "Sala VIP",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                ,
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(16.dp)
        ) {
            item {
                HeaderSectionVip()
            }
            item {
                CalendarSectionVip(
                    calendar = calendar,
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                if (isSunday) {
                    Text(
                        text = "No se pueden hacer reservas los domingos",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                ReservationButtonVip(
                    isEnabled = isDateValid,
                    onClick = {
                        val fechaSeleccionada = selectedDate?.time?.toString() ?: "Hoy"
                        navController?.navigate("PagoSalaVip?fecha=$fechaSeleccionada")
                    },
                    onBottomNavClick = onBottomNavClick,
                    navController = navController,
                    selectedDate = selectedDate ?: calendar
                )

            }
        }
    }
}

@Composable
private fun HeaderSectionVip() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "SALA VIP",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )
        Text(
            text = "Salas VIP Disponibles: 3",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Planifique su Reserva",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Seleccione la fecha deseada:",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CalendarSectionVip(
    calendar: Calendar,
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val tempCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
    val monthName = tempCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val shortWeekdays = Array(7) { i ->
        tempCalendar.apply { set(Calendar.DAY_OF_WEEK, i + 1) }
            .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_left),
                contentDescription = "Retroceder Mes",
                tint = Color.White
            )
        }
        Text(
            text = monthName?.uppercase() ?: "",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
                currentYear++
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_right),
                contentDescription = "Avanzar Mes",
                tint = Color.White
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        shortWeekdays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Column {
        var dayCounter = 1 - firstDayOfWeek
        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { dayOfWeek ->
                    val day = dayCounter + dayOfWeek
                    val isCurrentMonth = day in 1..daysInMonth
                    val date = if (isCurrentMonth) {
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, currentYear)
                            set(Calendar.MONTH, currentMonth)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                    } else null
                    val today = Calendar.getInstance()
                    val isToday = date?.let {
                        it.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                it.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                it.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                    } ?: false
                    val isPastDate = date?.let { it.before(today) && !isToday } ?: false
                    val isSunday = date?.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                    CalendarDayVip(
                        day = if (isCurrentMonth) day.toString() else "",
                        isSelected = selectedDate?.let {
                            it.get(Calendar.YEAR) == date?.get(Calendar.YEAR) &&
                                    it.get(Calendar.MONTH) == date?.get(Calendar.MONTH) &&
                                    it.get(Calendar.DAY_OF_MONTH) == date?.get(Calendar.DAY_OF_MONTH)
                        } ?: false,
                        isAvailable = isCurrentMonth && !isPastDate && !isSunday,
                        onClick = { if (isCurrentMonth && date != null && !isSunday) onDateSelected(date) }
                    )
                }
            }
            dayCounter += 7
            if (dayCounter > daysInMonth) return@Column
        }
    }
}

@Composable
private fun CalendarDayVip(
    day: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        !isAvailable -> Color.Red
        else -> Color.Transparent
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isAvailable -> Color.White
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day.isNotEmpty()) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = containerColor,
                onClick = onClick,
                enabled = isAvailable
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isAvailable) {
                        Text(
                            text = "No\nDisponible",
                            color = contentColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = day,
                            color = contentColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationButtonVip(
    isEnabled: Boolean,
    onClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    navController: NavController?,
    selectedDate: Calendar
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (isEnabled) {
            Button(
                onClick = {
                    val fechaFormateada = "${selectedDate.get(Calendar.DAY_OF_MONTH)}/" +
                            "${selectedDate.get(Calendar.MONTH) + 1}/" +
                            "${selectedDate.get(Calendar.YEAR)}"

                    navController?.navigate("PagoSalaVip?fecha=$fechaFormateada")

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0077B6),
                    contentColor = Color.White
                )
            ) {
                Text("Reservar ahora", fontWeight = FontWeight.Bold)
            }

        }

        Spacer(modifier = Modifier.height(70.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E5C94))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                iconRes = R.drawable.icon_inicio,
                label = "Inicio",
                isSelected = true,
                onClick = { onBottomNavClick("Inicio") }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewSalaVipReservationScreen() {
    MaterialTheme {
        SalaVipReservationScreen(
            onBottomNavClick = {},
            navController = NavController(LocalContext.current)
        )
    }
}