package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.viewmodel.UsuarioViewModel
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.ClienteViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel
import com.example.gestoreventos.view.EventoDetallesDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosEmpleadoListScreen(
    eventosEmpleado: List<Evento>,
    onBack: () -> Unit
) {
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Eventos",
                        color = BrandGold,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = BrandGold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Título de la sección
            Text(
                text = "Eventos Asignados",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (eventosEmpleado.isEmpty()) {
                // Estado vacío elegante
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = BrandGold.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 1.dp,
                                color = BrandGold.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📅",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tienes eventos asignados",
                                color = BrandGold,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Los eventos aparecerán aquí cuando se te asignen",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Listado elegante
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(eventosEmpleado) { evento ->
                        ElegantEventoItem(evento = evento, onClick = { eventoSeleccionado = evento })
                    }
                }
            }
        }
    }
    // Diálogo de detalles del evento (sin opción de editar)
    eventoSeleccionado?.let { evento ->
        EventoDetallesDialog(
            evento = evento,
            onDismiss = { eventoSeleccionado = null },
            onEditar = { /* No hacer nada - empleado no puede editar */ }
        )
    }
}

@Composable
fun ElegantEventoItem(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = BrandGold.copy(alpha = 0.2f)
            )
            .border(
                width = 1.dp,
                color = BrandGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Encabezado del evento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evento #${evento.id}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandGold
                    )
                )

                // Indicador de estado
                Card(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = BrandGold,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandGold.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ASIGNADO",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandGold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información principal
            Column {
                // Fecha y hora
                InfoSection(
                    label = "Fecha y Hora",
                    value = "${evento.fecha} • ${evento.horaInicio} - ${evento.horaFin}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Personas
                InfoSection(
                    label = "Número de Personas",
                    value = "${evento.numeroPersonas} personas"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dirección
                InfoSection(
                    label = "Dirección del Evento",
                    value = evento.direccionEvento
                )

                Spacer(modifier = Modifier.height(12.dp))

                // IDs adicionales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoSection(
                            label = "Cliente ID",
                            value = evento.idCliente.toString()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        InfoSection(
                            label = "Mobiliario ID",
                            value = evento.idMobiliario.toString()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(
                    label = "Servicio ID",
                    value = evento.idServicio.toString()
                )

                // Detalle del servicio (si existe)
                if (evento.detalleServicio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoSection(
                        label = "Detalle del Servicio",
                        value = evento.detalleServicio
                    )
                }
            }
        }
    }
}

@Composable
fun InfoSection(label: String, value: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}