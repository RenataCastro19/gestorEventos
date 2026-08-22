package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.model.Gasto
import com.example.gestoreventos.model.Pago
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.SuccessGreen
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.viewmodel.GastoViewModel
import com.example.gestoreventos.viewmodel.PagoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Un movimiento unificado: puede venir de un Gasto (salida) o un Pago (entrada)
private data class Movimiento(
    val titulo: String,
    val subtitulo: String,
    val monto: Double,
    val esIngreso: Boolean,
    val fecha: String
)

@Composable
fun CorteMensualScreen(
    gastoViewModel: GastoViewModel = viewModel(),
    pagoViewModel: PagoViewModel = viewModel()
) {
    var gastos by remember { mutableStateOf(listOf<Gasto>()) }
    var pagos by remember { mutableStateOf(listOf<Pago>()) }
    var mesActual by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        gastoViewModel.obtenerGastos { gastos = it }
        pagoViewModel.obtenerPagos { pagos = it }
    }

    val mesSeleccionado = String.format(
        Locale.getDefault(), "%04d-%02d",
        mesActual.get(Calendar.YEAR), mesActual.get(Calendar.MONTH) + 1
    )

    val gastosDelMes = gastos.filter { it.mes == mesSeleccionado }
    val pagosDelMes = pagos.filter { it.mes == mesSeleccionado }

    val totalIngresos = pagosDelMes.sumOf { it.monto }
    val totalGastos = gastosDelMes.sumOf { it.monto }
    val utilidad = totalIngresos - totalGastos

    val movimientos = remember(gastosDelMes, pagosDelMes) {
        (gastosDelMes.map {
            Movimiento(
                titulo = it.producto,
                subtitulo = it.nombreUsuario,
                monto = it.monto,
                esIngreso = false,
                fecha = it.fecha
            )
        } + pagosDelMes.map {
            Movimiento(
                titulo = it.tipo.replaceFirstChar { c -> c.uppercase() },
                subtitulo = "Pago de evento",
                monto = it.monto,
                esIngreso = true,
                fecha = it.fecha
            )
        }).sortedByDescending { parseFechaMovimiento(it.fecha) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Corte Mensual",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Selector de mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { mesActual = (mesActual.clone() as Calendar).apply { add(Calendar.MONTH, -1) } },
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandGold.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior", tint = BrandGold)
            }
            Text(
                text = getMonthYearString(mesActual),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            IconButton(
                onClick = { mesActual = (mesActual.clone() as Calendar).apply { add(Calendar.MONTH, 1) } },
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandGold.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente", tint = BrandGold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen del mes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                )
                .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                FilaResumen("Ingresos", totalIngresos, SuccessGreen)
                Spacer(modifier = Modifier.height(10.dp))
                FilaResumen("Gastos", totalGastos, ErrorRed)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Utilidad",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "$${String.format(Locale.getDefault(), "%.2f", utilidad)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (utilidad >= 0) SuccessGreen else ErrorRed
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Movimientos",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (movimientos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Sin movimientos",
                        tint = BrandGold.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sin movimientos este mes",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(movimientos) { mov ->
                    MovimientoCard(mov)
                }
            }
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, monto: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
        Text(
            text = "$${String.format(Locale.getDefault(), "%.2f", monto)}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
private fun MovimientoCard(movimiento: Movimiento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .border(width = 1.dp, color = CardBorder, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (movimiento.esIngreso) SuccessGreen else ErrorRed,
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = movimiento.titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${movimiento.fecha} · ${movimiento.subtitulo}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${if (movimiento.esIngreso) "+" else "-"}$${String.format(Locale.getDefault(), "%.2f", movimiento.monto)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (movimiento.esIngreso) SuccessGreen else ErrorRed
                )
            )
        }
    }
}

private fun parseFechaMovimiento(fecha: String): Long {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}