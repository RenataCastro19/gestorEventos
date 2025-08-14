package com.example.gestoreventos.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.*
import com.example.gestoreventos.viewmodel.*
import com.example.gestoreventos.ui.theme.BrandGold
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

// Funciones auxiliares (definidas al inicio para que estén disponibles)
fun parseFecha(fecha: String): Calendar {
    return try {
        val partes = fecha.split("/")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, partes[0].toInt())
        calendar.set(Calendar.MONTH, partes[1].toInt() - 1)
        calendar.set(Calendar.YEAR, partes[2].toInt())
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar
    } catch (e: Exception) {
        Calendar.getInstance()
    }
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

// Composable auxiliares (definidos antes de ser usados)
@Composable
fun ModernCalendarHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
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
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Mes anterior",
                        tint = Color.White,
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
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "Gestión de Eventos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    )
                }

                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Mes siguiente",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
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
    secondaryColor: Color,
    onDateSelected: (Calendar) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header de días de la semana
            ModernWeekDaysHeader(primaryColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Grid del calendario
            ModernCalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                eventos = eventos,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
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
                        color = primaryColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontSize = 14.sp
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
    secondaryColor: Color,
    onDateSelected: (Calendar) -> Unit
) {
    println("DEBUG: ModernCalendarGrid iniciado")
    val calendar = currentMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Ajustar para que la semana empiece en lunes
    // Calendar.MONDAY = 2, pero queremos que sea el primer día (0)
    val startOffset = when (firstDayOfWeek) {
        Calendar.SUNDAY -> 6    // Domingo va al final de la semana
        Calendar.MONDAY -> 0    // Lunes es el primer día
        Calendar.TUESDAY -> 1   // Martes es el segundo día
        Calendar.WEDNESDAY -> 2 // Miércoles es el tercer día
        Calendar.THURSDAY -> 3  // Jueves es el cuarto día
        Calendar.FRIDAY -> 4    // Viernes es el quinto día
        Calendar.SATURDAY -> 5  // Sábado es el sexto día
        else -> 0
    }

    val totalCells = startOffset + daysInMonth
    val weeks = (totalCells + 6) / 7 // Redondear hacia arriba para asegurar suficientes semanas

    println("DEBUG: firstDayOfWeek = $firstDayOfWeek")
    println("DEBUG: startOffset = $startOffset")
    println("DEBUG: daysInMonth = $daysInMonth")
    println("DEBUG: totalCells = $totalCells")
    println("DEBUG: weeks = $weeks")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val totalCells = weeks * 7

        repeat(weeks) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { dayOfWeek ->
                    val cellIndex = week * 7 + dayOfWeek

                    // Calcular la fecha real de la celda
                    val cellDate = currentMonth.clone() as Calendar
                    cellDate.set(Calendar.DAY_OF_MONTH, 1)
                    // Retroceder al primer día de la semana que contiene el primer día del mes
                    cellDate.add(Calendar.DAY_OF_MONTH, -startOffset)
                    // Avanzar hasta la celda actual
                    cellDate.add(Calendar.DAY_OF_MONTH, cellIndex)

                    val isCurrentMonth = cellDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                            cellDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR)

                    // Debug: imprimir información de cada celda
                    println("DEBUG: Week $week, Day $dayOfWeek, CellIndex $cellIndex, Date: ${cellDate.get(Calendar.DAY_OF_MONTH)}/${cellDate.get(Calendar.MONTH) + 1}, isCurrentMonth: $isCurrentMonth")

                    val hasEvents = eventos.any {
                        try {
                            val eventoDate = parseFecha(it.fecha)
                            isSameDay(eventoDate, cellDate)
                        } catch (e: Exception) {
                            false
                        }
                    }

                    val eventCount = eventos.count {
                        try {
                            val eventoDate = parseFecha(it.fecha)
                            isSameDay(eventoDate, cellDate)
                        } catch (e: Exception) {
                            false
                        }
                    }

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
    isCurrentMonth: Boolean = true // Nuevo parámetro
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else if (hasEvents) 4.dp else 0.dp,
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .shadow(
                elevation = animatedElevation,
                shape = CircleShape
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> primaryColor
                hasEvents && isCurrentMonth -> primaryColor.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.8f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                ),
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
                            isSelected -> Color.White
                            hasEvents && isCurrentMonth -> primaryColor
                            !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) // Días de otros meses en gris
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontSize = 16.sp
                    )
                )

                if (hasEvents && eventCount > 0 && isCurrentMonth) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (eventCount == 1) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (isSelected) Color.White else primaryColor,
                                        shape = CircleShape
                                    )
                            )
                        } else {
                            repeat(minOf(eventCount, 3)) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            color = if (isSelected) Color.White else primaryColor,
                                            shape = CircleShape
                                        )
                                )
                                if (it < minOf(eventCount, 3) - 1) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
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
    val eventosDelMes = eventos.filter { evento ->
        try {
            val eventoDate = parseFecha(evento.fecha)
            eventoDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                    eventoDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
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
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
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
                    text = "Eventos del mes",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${eventosDelMes.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
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
            text = "No hay eventos programados",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Los eventos del mes aparecerán aquí",
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
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
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
            // Indicador de fecha mejorado
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = try {
                                evento.fecha.split("/")[0]
                            } catch (e: Exception) {
                                "?"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
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
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del evento
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cliente?.nombre ?: "Cliente no especificado",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

            // Flecha indicadora
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalles",
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ModernEventoDetailDialog(
    evento: Evento,
    cliente: Cliente?,
    servicios: List<Servicio>,
    mobiliarios: List<Mobiliario>,
    usuarios: List<Usuario>,
    categoriasMobiliario: List<CategoriaMobiliario>,
    primaryColor: Color,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header del dialog
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Detalles del Evento",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Evento #${evento.id}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Contenido scrolleable
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información general
                    item {
                        ModernDetailSection(
                            title = "Información General",
                            icon = Icons.Default.Info,
                            primaryColor = primaryColor
                        ) {
                            ModernDetailItem("Fecha", evento.fecha, Icons.Default.CalendarToday)
                            ModernDetailItem("Horario", "${evento.horaInicio} - ${evento.horaFin}", Icons.Default.AccessTime)
                            ModernDetailItem("Personas", "${evento.numeroPersonas}", Icons.Default.People)
                            ModernDetailItem("Ubicación", evento.direccionEvento, Icons.Default.LocationOn)
                            if (evento.detalleServicio.isNotBlank()) {
                                ModernDetailItem("Comentarios", evento.detalleServicio, Icons.Default.Comment)
                            }
                        }
                    }

                    // Información del cliente
                    item {
                        ModernDetailSection(
                            title = "Cliente",
                            icon = Icons.Default.Person,
                            primaryColor = primaryColor
                        ) {
                            ModernDetailItem("Nombre", cliente?.nombre ?: "No especificado", Icons.Default.Badge)
                            ModernDetailItem("Teléfono", cliente?.telefono ?: "No especificado", Icons.Default.Phone)
                        }
                    }

                    // Servicios seleccionados
                    if (evento.serviciosSeleccionados.isNotEmpty()) {
                        item {
                            ModernDetailSection(
                                title = "Servicios",
                                icon = Icons.Default.RoomService,
                                primaryColor = primaryColor
                            ) {
                                evento.serviciosSeleccionados.forEach { servicioSeleccionado ->
                                    val servicio = servicios.find { it.id == servicioSeleccionado.idServicio }
                                    ModernDetailItem(
                                        "Servicio",
                                        servicio?.nombre ?: "Servicio no encontrado",
                                        Icons.Default.Star
                                    )

                                    servicioSeleccionado.categoriasSeleccionadas.forEach { categoria ->
                                        ModernDetailItem(
                                            categoria.nombreCategoria,
                                            categoria.opcionesSeleccionadas.joinToString(", "),
                                            Icons.Default.List
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Mobiliario seleccionado
                    if (evento.idMobiliario.isNotBlank()) {
                        val idsMobiliarios = evento.idMobiliario.split(",").filter { it.isNotEmpty() }
                        val mobiliariosEvento = mobiliarios.filter { it.id in idsMobiliarios }

                        if (mobiliariosEvento.isNotEmpty()) {
                            item {
                                ModernDetailSection(
                                    title = "Mobiliario",
                                    icon = Icons.Default.Chair,
                                    primaryColor = primaryColor
                                ) {
                                    mobiliariosEvento.forEach { mobiliario ->
                                        val categoria = categoriasMobiliario.find { it.id == mobiliario.idCategoria }
                                        ModernDetailItem(
                                            "Mobiliario",
                                            "${categoria?.nombre ?: "Categoría no encontrada"} - ${mobiliario.color}",
                                            Icons.Default.TableRestaurant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Empleados asignados
                    if (evento.listaIdsEmpleados.isNotEmpty()) {
                        val empleadosEvento = usuarios.filter { it.id in evento.listaIdsEmpleados }

                        if (empleadosEvento.isNotEmpty()) {
                            item {
                                ModernDetailSection(
                                    title = "Empleados Asignados",
                                    icon = Icons.Default.Group,
                                    primaryColor = primaryColor
                                ) {
                                    empleadosEvento.forEach { empleado ->
                                        ModernDetailItem(
                                            "Empleado",
                                            "${empleado.nombre} ${empleado.apellidoPaterno}",
                                            Icons.Default.PersonOutline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDetailSection(
    title: String,
    icon: ImageVector,
    primaryColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                )
            }
            content()
        }
    }
}

@Composable
fun ModernDetailItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    eventoViewModel: EventoViewModel = viewModel(),
    clienteViewModel: ClienteViewModel = viewModel(),
    servicioViewModel: ServicioViewModel = viewModel(),
    mobiliarioViewModel: MobiliarioViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel(),
    categoriaMobiliarioViewModel: CategoriaMobiliarioViewModel = viewModel(),
    usuarioActual: Usuario? = null, // Parámetro para el usuario actual
    modifier: Modifier = Modifier
) {
    println("DEBUG: CalendarioScreen iniciado")
    // Estados para los datos
    var eventosOriginales by remember { mutableStateOf(listOf<Evento>()) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }
    var servicios by remember { mutableStateOf(listOf<Servicio>()) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var usuarios by remember { mutableStateOf(listOf<Usuario>()) }
    var categoriasMobiliario by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }

    // Eventos filtrados según el rol del usuario
    val eventos = remember(eventosOriginales, usuarioActual) {
        when (usuarioActual?.rol) {
            "empleado" -> {
                // Para empleados, mostrar solo eventos donde están asignados
                eventosOriginales.filter { evento ->
                    evento.listaIdsEmpleados.contains(usuarioActual.id)
                }
            }
            "admin", "super_admin" -> {
                // Para admin y super_admin, mostrar todos los eventos
                eventosOriginales
            }
            else -> {
                // Si no hay usuario logueado, no mostrar eventos
                emptyList()
            }
        }
    }

    // Estados del calendario
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Evento?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos
    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventos { eventosOriginales = it }
        clienteViewModel.obtenerClientes { clientes = it }
        servicioViewModel.obtenerServicios { servicios = it }
        mobiliarioViewModel.obtenerMobiliario { mobiliarios = it }
        usuarioViewModel.obtenerUsuarios { usuarios = it }
        categoriaMobiliarioViewModel.obtenerCategorias {
            categoriasMobiliario = it
            isLoading = false
        }
    }

    // Tema de colores para el calendario
    val primaryColor = BrandGold
    val secondaryColor = primaryColor.copy(alpha = 0.1f)
    val accentColor = Color(0xFF6C63FF)
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = primaryColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    // Header del calendario mejorado
                    ModernCalendarHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                        },
                        onNextMonth = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                        },
                        primaryColor = primaryColor
                    )
                }

                item {
                    // Calendario principal mejorado
                    ModernCalendarCard(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        eventos = eventos,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        onDateSelected = { date ->
                            selectedDate = date
                            val eventosDelDia = eventos.filter { evento ->
                                try {
                                    val eventoDate = parseFecha(evento.fecha)
                                    isSameDay(eventoDate, date)
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
                    // Sección de eventos del mes
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

    // Dialog mejorado para mostrar detalles del evento
    if (showEventDialog && selectedEvent != null) {
        ModernEventoDetailDialog(
            evento = selectedEvent!!,
            cliente = clientes.find { it.id == selectedEvent!!.idCliente },
            servicios = servicios,
            mobiliarios = mobiliarios,
            usuarios = usuarios,
            categoriasMobiliario = categoriasMobiliario,
            primaryColor = primaryColor,
            onDismiss = {
                showEventDialog = false
                selectedEvent = null
            }
        )
    }
}