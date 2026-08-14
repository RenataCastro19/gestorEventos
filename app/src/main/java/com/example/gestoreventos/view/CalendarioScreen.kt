package com.example.gestoreventos.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestoreventos.model.*
import com.example.gestoreventos.viewmodel.*
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.WarningGold
import com.example.gestoreventos.ui.theme.WarningGoldBg
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import com.example.gestoreventos.utils.DateUtils

// Funciones auxiliares
fun parseFecha(fecha: String): Calendar {
    return DateUtils.parseFecha(fecha)
}

fun getMonthYearString(calendar: Calendar): String {
    val meses = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val mes = meses[calendar.get(Calendar.MONTH)]
    val año = calendar.get(Calendar.YEAR)
    return "$mes $año"
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

@Composable
fun ModernCalendarHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    primaryColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(color = primaryColor, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = BrandBlack.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Mes anterior",
                    tint = BrandBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getMonthYearString(currentMonth),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = "Gestión de Eventos",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BrandBlack.copy(alpha = 0.7f)
                    )
                )
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = BrandBlack.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Mes siguiente",
                    tint = BrandBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ModernCalendarCard(
    currentMonth: Calendar,
    selectedDate: Calendar,
    eventos: List<Evento>,
    primaryColor: Color,
    onDateSelected: (Calendar) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            ModernWeekDaysHeader(primaryColor)
            Spacer(modifier = Modifier.height(16.dp))
            ModernCalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                eventos = eventos,
                primaryColor = primaryColor,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
fun ModernWeekDaysHeader(primaryColor: Color) {
    val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        diasSemana.forEach { dia ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(
                        color = WarningGoldBg,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarningGold
                    )
                )
            }
        }
    }
}

@Composable
fun ModernCalendarGrid(
    currentMonth: Calendar,
    selectedDate: Calendar,
    eventos: List<Evento>,
    primaryColor: Color,
    onDateSelected: (Calendar) -> Unit
) {
    val calendar = currentMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Ajustar para que la semana empiece en lunes (Calendar.MONDAY = 2 -> queremos que sea el día 0)
    val startOffset = when (firstDayOfWeek) {
        Calendar.SUNDAY -> 6
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 0
    }

    val totalCells = startOffset + daysInMonth
    val weeks = (totalCells + 6) / 7

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(weeks) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { dayOfWeek ->
                    val cellIndex = week * 7 + dayOfWeek

                    val cellDate = currentMonth.clone() as Calendar
                    cellDate.set(Calendar.DAY_OF_MONTH, 1)
                    cellDate.add(Calendar.DAY_OF_MONTH, -startOffset)
                    cellDate.add(Calendar.DAY_OF_MONTH, cellIndex)

                    val isCurrentMonth = cellDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                            cellDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR)

                    val eventCount = eventos.count {
                        try {
                            isSameDay(parseFecha(it.fecha), cellDate)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    val hasEvents = eventCount > 0

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        ModernCalendarDay(
                            date = cellDate,
                            isSelected = isCurrentMonth && isSameDay(cellDate, selectedDate),
                            hasEvents = hasEvents,
                            eventCount = eventCount,
                            primaryColor = primaryColor,
                            onClick = { if (isCurrentMonth) onDateSelected(cellDate) },
                            isCurrentMonth = isCurrentMonth
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCalendarDay(
    date: Calendar,
    isSelected: Boolean,
    hasEvents: Boolean,
    eventCount: Int,
    primaryColor: Color,
    onClick: () -> Unit,
    isCurrentMonth: Boolean = true
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        animationSpec = tween(200),
        label = "dayElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .shadow(
                elevation = animatedElevation,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> primaryColor
                hasEvents && isCurrentMonth -> WarningGoldBg
                else -> Color.Transparent
            }
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.get(Calendar.DAY_OF_MONTH).toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (hasEvents || isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isSelected -> BrandBlack
                            hasEvents && isCurrentMonth -> WarningGold
                            !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                )

                if (hasEvents && isCurrentMonth) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(minOf(eventCount, 3)) { index ->
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        color = if (isSelected) BrandBlack else WarningGold,
                                        shape = CircleShape
                                    )
                            )
                            if (index < minOf(eventCount, 3) - 1) {
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernEventSection(
    currentMonth: Calendar,
    eventos: List<Evento>,
    clientes: List<Cliente>,
    primaryColor: Color,
    onEventClick: (Evento) -> Unit
) {
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val eventosDelMes = eventos.filter { evento ->
        try {
            val eventoDate = parseFecha(evento.fecha)
            eventoDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                    eventoDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                    !eventoDate.before(hoy)
        } catch (e: Exception) {
            false
        }
    }.sortedBy { evento ->
        try {
            parseFecha(evento.fecha).timeInMillis
        } catch (e: Exception) {
            0L
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Eventos próximos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    ),
                    modifier = Modifier.weight(1f)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningGoldBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${eventosDelMes.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarningGold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (eventosDelMes.isEmpty()) {
                ModernEmptyState(primaryColor)
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    eventosDelMes.forEach { evento ->
                        ModernEventoCard(
                            evento = evento,
                            cliente = clientes.find { it.id == evento.idCliente },
                            primaryColor = primaryColor,
                            onClick = { onEventClick(evento) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernEmptyState(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EventAvailable,
            contentDescription = "Sin eventos",
            tint = primaryColor.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No hay eventos próximos",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Los eventos futuros del mes aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModernEventoCard(
    evento: Evento,
    cliente: Cliente?,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de fecha
            Card(
                modifier = Modifier.size(56.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = try { evento.fecha.split("/")[0] } catch (e: Exception) { "?" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = BrandBlack,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = try {
                                val mes = evento.fecha.split("/")[1].toInt()
                                arrayOf("", "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
                                    "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")[mes]
                            } catch (e: Exception) {
                                "???"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandBlack,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del evento
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente?.nombre ?: "Cliente no especificado",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Horario",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${evento.horaInicio} - ${evento.horaFin}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Personas",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${evento.numeroPersonas} personas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalles",
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    eventoViewModel: EventoViewModel = viewModel(),
    clienteViewModel: ClienteViewModel = viewModel(),
    usuarioActual: Usuario? = null,
    onEditarEventoClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var eventosOriginales by remember { mutableStateOf(listOf<Evento>()) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }

    // Eventos filtrados según el rol del usuario
    val eventos = remember(eventosOriginales, usuarioActual) {
        when (usuarioActual?.rol) {
            "empleado" -> eventosOriginales.filter { it.listaIdsEmpleados.contains(usuarioActual.id) }
            "admin", "super_admin" -> eventosOriginales
            else -> emptyList()
        }
    }

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Evento?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventos { eventosOriginales = it }
        clienteViewModel.obtenerClientes {
            clientes = it
            isLoading = false
        }
    }

    val primaryColor = BrandGold

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor, modifier = Modifier.size(48.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ModernCalendarHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = {
                            currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                        },
                        onNextMonth = {
                            currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                        },
                        primaryColor = primaryColor
                    )
                }

                item {
                    ModernCalendarCard(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        eventos = eventos,
                        primaryColor = primaryColor,
                        onDateSelected = { date ->
                            selectedDate = date
                            val eventosDelDia = eventos.filter { evento ->
                                try {
                                    isSameDay(parseFecha(evento.fecha), date)
                                } catch (e: Exception) {
                                    false
                                }
                            }
                            if (eventosDelDia.isNotEmpty()) {
                                selectedEvent = eventosDelDia.first()
                                showEventDialog = true
                            }
                        }
                    )
                }

                item {
                    ModernEventSection(
                        currentMonth = currentMonth,
                        eventos = eventos,
                        clientes = clientes,
                        primaryColor = primaryColor,
                        onEventClick = { evento ->
                            selectedEvent = evento
                            showEventDialog = true
                        }
                    )
                }
            }
        }
    }

    // Reutiliza el mismo diálogo de detalle que EventosListScreen — ya no hay una versión
    // aparte para el calendario, así cualquier cambio futuro (PDF, edición, texto seleccionable)
    // aplica automáticamente aquí también.
    if (showEventDialog && selectedEvent != null) {
        EventoDetallesDialog(
            evento = selectedEvent!!,
            onDismiss = {
                showEventDialog = false
                selectedEvent = null
            },
            onEditar = {
                val idEvento = selectedEvent?.id
                showEventDialog = false
                selectedEvent = null
                if (idEvento != null) onEditarEventoClick(idEvento)
            },
            currentUser = usuarioActual
        )
    }
}