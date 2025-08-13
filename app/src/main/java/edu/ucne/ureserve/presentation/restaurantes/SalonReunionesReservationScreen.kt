package edu.ucne.ureserve.presentation.salareuniones

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.dashboard.BottomNavItem
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonReunionesReservationScreen(
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
                            painter = painterResource(id = R.drawable.salon),
                            contentDescription = "Salón de Reuniones",
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
                HeaderSectionReuniones()
            }
            item {
                CalendarSectionReuniones(
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

                ReservationButtonReuniones(
                    isEnabled = isDateValid,
                    onBottomNavClick = onBottomNavClick,
                    navController = navController,
                    selectedDate = selectedDate ?: calendar
                )
            }
        }
    }
}

@Composable
private fun HeaderSectionReuniones() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "SALÓN DE REUNIONES",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )
        Text(
            text = "Salones de Reuniones Disponibles: 2",
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
private fun CalendarSectionReuniones(
    calendar: Calendar,
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    var currentMonth by remember { mutableIntStateOf(calendar[Calendar.MONTH]) }
    var currentYear by remember { mutableIntStateOf(calendar[Calendar.YEAR]) }

    val monthData = rememberMonthData(currentYear, currentMonth)
    val today = Calendar.getInstance()

    Column {
        MonthNavigationHeader(
            monthName = monthData.monthName,
            onPreviousMonth = {
                currentMonth = if (currentMonth == 0) 11 else currentMonth - 1
                if (currentMonth == 11) currentYear--
            },
            onNextMonth = {
                currentMonth = if (currentMonth == 11) 0 else currentMonth + 1
                if (currentMonth == 0) currentYear++
            }
        )

        WeekdaysHeader(monthData.shortWeekdays)

        Spacer(modifier = Modifier.height(8.dp))

        CalendarGrid(
            monthData = monthData,
            currentYear = currentYear,
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            today = today,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
private fun MonthNavigationHeader(
    monthName: String?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
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
        IconButton(onClick = onNextMonth) {
            Icon(
                painter = painterResource(id = R.drawable.icon_right),
                contentDescription = "Avanzar Mes",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun WeekdaysHeader(shortWeekdays: List<String>) {
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
}

@Composable
private fun CalendarGrid(
    monthData: MonthData,
    currentYear: Int,
    currentMonth: Int,
    selectedDate: Calendar?,
    today: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    var dayCounter = 1 - monthData.firstDayOfWeek

    Column {
        repeat(6) {
            if (dayCounter > monthData.daysInMonth) return@Column

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { dayOfWeek ->
                    val day = dayCounter + dayOfWeek
                    val date = createDateIfValid(day, monthData.daysInMonth, currentYear, currentMonth)

                    if (date != null) {
                        val isToday = isToday(date, today)
                        val isPastDate = date.before(today) && !isToday
                        val isSunday = date[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY

                        CalendarDayReuniones(
                            day = day.toString(),
                            isSelected = isSelected(date, selectedDate),
                            isAvailable = !isPastDate && !isSunday,
                            onClick = { if (!isSunday) onDateSelected(date) }
                        )
                    } else {
                        CalendarDayReuniones(
                            day = "",
                            isSelected = false,
                            isAvailable = false,
                            onClick = {}
                        )
                    }
                }
            }
            dayCounter += 7
        }
    }
}

private data class MonthData(
    val daysInMonth: Int,
    val firstDayOfWeek: Int,
    val monthName: String?,
    val shortWeekdays: List<String>
)

@Composable
private fun rememberMonthData(year: Int, month: Int): MonthData {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar[Calendar.DAY_OF_WEEK] - 1
    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

    val shortWeekdays = List(7) { i ->
        calendar.apply { set(Calendar.DAY_OF_WEEK, i + 1) }
            .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            ?: "??"
    }

    return MonthData(daysInMonth, firstDayOfWeek, monthName, shortWeekdays)
}

private fun createDateIfValid(day: Int, daysInMonth: Int, year: Int, month: Int): Calendar? {
    return if (day in 1..daysInMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
    } else null
}

private fun isToday(date: Calendar, today: Calendar): Boolean {
    return date[Calendar.YEAR] == today[Calendar.YEAR] &&
            date[Calendar.MONTH] == today[Calendar.MONTH] &&
            date[Calendar.DAY_OF_MONTH] == today[Calendar.DAY_OF_MONTH]
}

private fun isSelected(date: Calendar, selectedDate: Calendar?): Boolean {
    return selectedDate?.let {
        it[Calendar.YEAR] == date[Calendar.YEAR] &&
                it[Calendar.MONTH] == date[Calendar.MONTH] &&
                it[Calendar.DAY_OF_MONTH] == date[Calendar.DAY_OF_MONTH]
    } ?: false
}

@Composable
private fun CalendarDayReuniones(
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
private fun ReservationButtonReuniones(
    isEnabled: Boolean,
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
                    navController?.navigate("PagoSalon?fecha=$fechaFormateada")
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
fun PreviewSalonReunionesReservationScreen() {
    MaterialTheme {
        SalonReunionesReservationScreen(
            onBottomNavClick = {},
            navController = null
        )
    }
}