package edu.ucne.ureserve.presentation.laboratorios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaboratorioReservationScreen(
    onBottomNavClick: (String) -> Unit = {},
    navController: NavController
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
                            painter = painterResource(id = R.drawable.icon_laboratorio),
                            contentDescription = "Laboratorio",
                            modifier = Modifier.size(60.dp)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF023E8A))
                .padding(16.dp)
        ) {
            item {
                HeaderSection()
            }
            item {
                CalendarSection(
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
                ReservationButton(
                    isEnabled = isDateValid,
                    onClick = {
                        selectedDate?.let { date ->
                            navController.navigate("LaboratorioList/${date.timeInMillis}")
                        }
                    },
                    onBottomNavClick = onBottomNavClick
                )
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
    ) {
        Text(
            text = "LABORATORIOS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
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
private fun CalendarSection(
    calendar: Calendar,
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    val tempCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1

    val monthName = tempCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""

    val shortWeekdays = Array(7) { i ->
        tempCalendar.apply { set(Calendar.DAY_OF_WEEK, i + 1) }
            .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
    }

    fun isSameDate(cal1: Calendar?, cal2: Calendar?): Boolean {
        if (cal1 == null || cal2 == null) return false
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    fun createDate(day: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
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
            text = monthName.uppercase(),
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
                for (dayOfWeek in 0 until 7) {
                    val day = dayCounter + dayOfWeek
                    val isCurrentMonth = day in 1..daysInMonth
                    val date = if (isCurrentMonth) createDate(day) else null

                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val isToday = isSameDate(date, today)

                    val isPastDate = date?.before(today) == true && !isToday
                    val isSunday = date?.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

                    CalendarDay(
                        day = if (isCurrentMonth) day.toString() else "",
                        isSelected = isSameDate(selectedDate, date),
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
private fun CalendarDay(
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
private fun ReservationButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    onBottomNavClick: (String) -> Unit
) {
    Column {
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reservar", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
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