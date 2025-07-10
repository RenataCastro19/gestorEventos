package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.viewmodel.UsuarioViewModel
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.ClienteViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel

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
                        EventoCard(evento = evento, onClick = { eventoSeleccionado = evento })
                    }
                }
            }
        }
    }

    // Diálogo de detalles del evento específico para empleados (sin opción de editar)
    eventoSeleccionado?.let { evento ->
        EventoDetallesDialog(
            evento = evento,
            onDismiss = { eventoSeleccionado = null }
        )
    }
}

@Composable
fun EventoDetallesDialog(
    evento: Evento,
    onDismiss: () -> Unit
) {
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val servicioViewModel: ServicioViewModel = viewModel()
    val mobiliarioViewModel: MobiliarioViewModel = viewModel()
    val clienteViewModel: ClienteViewModel = viewModel()
    val categoriaMobiliarioViewModel: CategoriaMobiliarioViewModel = viewModel()

    var empleados by remember { mutableStateOf(listOf<Usuario>()) }
    var servicio by remember { mutableStateOf<Servicio?>(null) }
    var todosLosServicios by remember { mutableStateOf(listOf<Servicio>()) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var categoriasMobiliario by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var clientesCargados by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        usuarioViewModel.obtenerUsuarios { listaEmpleados ->
            empleados = listaEmpleados.filter { it.id in evento.listaIdsEmpleados }
        }
        servicioViewModel.obtenerServicios { listaServicios ->
            todosLosServicios = listaServicios
            servicio = listaServicios.find { it.id == evento.idServicio }
        }
        mobiliarioViewModel.obtenerMobiliario { listaMobiliarios ->
            val idsMobiliarios = evento.idMobiliario.split(",").filter { it.isNotEmpty() }
            mobiliarios = listaMobiliarios.filter { it.id in idsMobiliarios }
        }
        clienteViewModel.obtenerClientes { listaClientes ->
            cliente = listaClientes.find { it.id == evento.idCliente }
            clientesCargados = true
        }
        categoriaMobiliarioViewModel.obtenerCategorias { listaCategorias ->
            categoriasMobiliario = listaCategorias
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 16.dp,
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
                // Header con título y solo botón de cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles del Evento",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandGold
                        )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido con scroll
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Información básica
                    SeccionDetalles(
                        titulo = "Información General"
                    ) {
                        ItemDetalle("ID del Evento", evento.id)
                        ItemDetalle("Fecha", evento.fecha)
                        ItemDetalle("Horario", "${evento.horaInicio} - ${evento.horaFin}")
                        ItemDetalle("Número de Personas", "${evento.numeroPersonas} personas")
                        ItemDetalle("Dirección del Evento", evento.direccionEvento)
                        if (evento.detalleServicio.isNotBlank()) {
                            ItemDetalle("Comentarios", evento.detalleServicio)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Datos del Cliente
                    SeccionDetalles(
                        titulo = "Datos del Cliente"
                    ) {
                        if (!clientesCargados) {
                            ItemDetalle("Estado", "Cargando datos del cliente...")
                        } else if (cliente != null) {
                            ItemDetalle("Nombre", cliente!!.nombre)
                            ItemDetalle("Teléfono", cliente!!.telefono)
                            ItemDetalle("ID del Cliente", cliente!!.id)
                        } else {
                            ItemDetalle("Cliente", "No encontrado")
                            ItemDetalle("ID del Cliente", evento.idCliente.ifEmpty { "Sin asignar" })
                            ItemDetalle("Estado", "Cliente no encontrado en la base de datos")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Servicios con categorías y opciones seleccionadas
                    if (evento.serviciosSeleccionados.isNotEmpty()) {
                        SeccionDetalles(
                            titulo = "Servicios"
                        ) {
                            evento.serviciosSeleccionados.forEach { servicioSeleccionado ->
                                // Buscar el servicio para obtener su nombre
                                val nombreServicio = todosLosServicios.find { it.id == servicioSeleccionado.idServicio }?.nombre
                                    ?: "Servicio ${servicioSeleccionado.idServicio}"

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "• $nombreServicio",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    servicioSeleccionado.categoriasSeleccionadas.forEach { categoria ->
                                        val opcionesTexto = categoria.opcionesSeleccionadas.joinToString(", ")
                                        Text(
                                            text = "  - ${categoria.nombreCategoria}: $opcionesTexto",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                            ),
                                            modifier = Modifier.padding(start = 16.dp, bottom = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Empleados
                    SeccionDetalles(
                        titulo = "Personal Asignado (${empleados.size} empleados)"
                    ) {
                        if (empleados.isNotEmpty()) {
                            empleados.forEach { empleado ->
                                ItemDetalle(
                                    "Empleado",
                                    "${empleado.nombre} ${empleado.apellidoPaterno} ${empleado.apellidoMaterno} - ${empleado.rol}"
                                )
                            }
                        } else {
                            ItemDetalle("Empleados", "Sin empleados asignados")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mobiliario
                    SeccionDetalles(
                        titulo = "Mobiliario Asignado (${mobiliarios.size} items)"
                    ) {
                        if (mobiliarios.isNotEmpty()) {
                            mobiliarios.forEach { mobiliario ->
                                val categoria = categoriasMobiliario.find { it.id == mobiliario.idCategoria }
                                val nombreCategoria = categoria?.nombre ?: "Sin categoría"
                                ItemDetalle(
                                    "Mobiliario",
                                    "$nombreCategoria - Color: ${mobiliario.color}"
                                )
                            }
                        } else {
                            ItemDetalle("Mobiliario", "Sin mobiliario asignado")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionDetalles(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BrandGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            contenido()
        }
    }
}

@Composable
fun ItemDetalle(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$etiqueta:",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {
    val servicioViewModel: ServicioViewModel = viewModel()
    var nombreServicio by remember { mutableStateOf("") }

    // Cargar el nombre del servicio
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
            // ID del evento
            Text(
                text = "Evento #${evento.id}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Fecha (día, mes y año)
            Text(
                text = evento.fecha,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Hora
            Text(
                text = "${evento.horaInicio} - ${evento.horaFin}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Nombre del servicio
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

@Composable
fun InformacionEvento(label: String, value: String) {
    Column {
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
}