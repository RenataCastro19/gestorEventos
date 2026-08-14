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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventAvailable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.viewmodel.EventoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosEmpleadoListScreen(
    eventosEmpleado: List<Evento>,
    usuarioActual: Usuario,
    onBack: () -> Unit,
    onCalendarioClick: () -> Unit = {}
) {
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
    val eventoViewModel: EventoViewModel = viewModel()
    var eventos by remember { mutableStateOf(listOf<Evento>()) }
    var eventosEmpleadoActual by remember { mutableStateOf(listOf<Evento>()) }

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventos { listaEventos ->
            eventos = listaEventos
        }
    }

    LaunchedEffect(eventos, usuarioActual) {
        eventosEmpleadoActual = eventos.filter { it.listaIdsEmpleados.contains(usuarioActual.id) }
    }

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
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botón de calendario
            Button(
                onClick = onCalendarioClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGold,
                    contentColor = BrandBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Ver Calendario",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (eventosEmpleadoActual.isEmpty()) {
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
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color.Black.copy(alpha = 0.12f)
                            )
                            .border(
                                width = 1.dp,
                                color = CardBorder,
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
                            Icon(
                                imageVector = Icons.Filled.EventAvailable,
                                contentDescription = "Sin eventos",
                                tint = BrandGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(56.dp)
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(eventosEmpleadoActual) { evento ->
                        EventoCard(evento = evento, onClick = { eventoSeleccionado = evento })
                    }
                }
            }
        }
    }

    // Reutiliza el mismo diálogo de detalle de EventosListScreen. Al pasarle el usuario
    // empleado como currentUser, el propio diálogo oculta los botones de Editar/PDF
    // automáticamente (esa lógica ya vive ahí), así que aquí no hace falta una copia aparte.
    eventoSeleccionado?.let { evento ->
        EventoDetallesDialog(
            evento = evento,
            onDismiss = { eventoSeleccionado = null },
            onEditar = {},
            currentUser = usuarioActual
        )
    }
}

@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {
    val servicioViewModel: ServicioViewModel = viewModel()
    var nombreServicio by remember { mutableStateOf("") }

    LaunchedEffect(evento.idServicio) {
        if (evento.idServicio.isNotEmpty()) {
            servicioViewModel.obtenerServicios { listaServicios ->
                val servicio = listaServicios.find { it.id == evento.idServicio }
                nombreServicio = servicio?.nombre ?: "Servicio no encontrado"
            }
        } else {
            nombreServicio = "Sin servicio asignado"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = CardBorder,
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
            Text(
                text = "Evento #${evento.id}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = evento.fecha,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "${evento.horaInicio} - ${evento.horaFin}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = nombreServicio,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}