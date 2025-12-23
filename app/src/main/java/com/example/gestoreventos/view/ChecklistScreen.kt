package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestoreventos.model.*
import com.example.gestoreventos.viewmodel.SuperAdminViewModel
import com.example.gestoreventos.viewmodel.EventoViewModel
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    eventoId: String,
    onBackClick: () -> Unit,
    superAdminViewModel: SuperAdminViewModel = viewModel(),
    eventoViewModel: EventoViewModel = viewModel(),
    servicioViewModel: ServicioViewModel = viewModel()
) {
    val eventos by superAdminViewModel.eventos.collectAsState()
    val evento = eventos.find { it.id == eventoId }

    var todosLosServicios by remember { mutableStateOf(listOf<Servicio>()) }
    var checklistsEvento by remember { mutableStateOf<Map<String, ChecklistEvento>>(emptyMap()) }
    var cambiosPendientes by remember { mutableStateOf(false) }

    // Cargar servicios
    LaunchedEffect(Unit) {
        servicioViewModel.obtenerServicios { servicios ->
            todosLosServicios = servicios
        }
    }

    // Inicializar checklists desde el evento
    LaunchedEffect(evento) {
        if (evento != null) {
            if (evento.checklists.isEmpty()) {
                // Crear checklists desde los templates de los servicios
                val nuevosChecklists = mutableMapOf<String, ChecklistEvento>()

                evento.serviciosSeleccionados.forEach { servicioSel ->
                    val servicio = todosLosServicios.find { it.id == servicioSel.idServicio }
                    if (servicio != null && servicio.checklistTemplate.isNotEmpty()) {
                        val checklistEvento = ChecklistEvento(
                            idServicio = servicio.id,
                            nombreServicio = servicio.nombre,
                            categorias = servicio.checklistTemplate.map { categoria ->
                                ChecklistCategoriaEvento(
                                    nombre = categoria.nombre,
                                    items = categoria.items.map { item ->
                                        ChecklistItem(nombre = item, completado = false)
                                    }
                                )
                            }
                        )
                        nuevosChecklists[servicio.id] = checklistEvento
                    }
                }
                checklistsEvento = nuevosChecklists
            } else {
                checklistsEvento = evento.checklists
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Checklist del Evento",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        evento?.let {
                            Text(
                                "ID: ${it.id} - ${it.fecha}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = BrandGold
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (evento == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Evento no encontrado")
                }
            } else if (checklistsEvento.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "No hay checklist configurado para este evento",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Los servicios de este evento no tienen checklist definido",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            } else {
                // Indicador de progreso
                val totalItems = checklistsEvento.values.sumOf { checklist ->
                    checklist.categorias.sumOf { it.items.size }
                }
                val completedItems = checklistsEvento.values.sumOf { checklist ->
                    checklist.categorias.sumOf { categoria ->
                        categoria.items.count { it.completado }
                    }
                }
                val progreso = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Progreso Total",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "$completedItems / $totalItems",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = progreso,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = BrandGold,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                // Lista de checklists por servicio
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    checklistsEvento.forEach { (servicioId, checklist) ->
                        item {
                            ChecklistServicioCard(
                                checklist = checklist,
                                onItemToggle = { categoriaIndex, itemIndex ->
                                    val nuevosChecklists = checklistsEvento.toMutableMap()
                                    val checklistActual = nuevosChecklists[servicioId]

                                    if (checklistActual != null) {
                                        val nuevasCategorias = checklistActual.categorias.toMutableList()
                                        val categoria = nuevasCategorias[categoriaIndex]
                                        val nuevosItems = categoria.items.toMutableList()

                                        nuevosItems[itemIndex] = nuevosItems[itemIndex].copy(
                                            completado = !nuevosItems[itemIndex].completado
                                        )

                                        nuevasCategorias[categoriaIndex] = categoria.copy(items = nuevosItems)
                                        nuevosChecklists[servicioId] = checklistActual.copy(categorias = nuevasCategorias)

                                        checklistsEvento = nuevosChecklists
                                        cambiosPendientes = true
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar
                Button(
                    onClick = {
                        val eventoActualizado = evento.copy(checklists = checklistsEvento)
                        eventoViewModel.actualizarEvento(
                            eventoActualizado,
                            onSuccess = {
                                cambiosPendientes = false
                            },
                            onFailure = { }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = cambiosPendientes,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGold,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (cambiosPendientes) "Guardar Cambios" else "Sin Cambios",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistServicioCard(
    checklist: ChecklistEvento,
    onItemToggle: (categoriaIndex: Int, itemIndex: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
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
        Column(modifier = Modifier.padding(16.dp)) {
            // Título del servicio
            Text(
                text = checklist.nombreServicio,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Categorías
            checklist.categorias.forEachIndexed { categoriaIndex, categoria ->
                if (categoriaIndex > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Título de la categoría
                Text(
                    text = categoria.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Items de la categoría
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categoria.items.forEachIndexed { itemIndex, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.completado,
                                onCheckedChange = {
                                    onItemToggle(categoriaIndex, itemIndex)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandGold,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.nombre,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (item.completado)
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (item.completado)
                                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    else
                                        androidx.compose.ui.text.style.TextDecoration.None
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}