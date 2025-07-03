package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.viewmodel.SuperAdminViewModel
import com.example.gestoreventos.viewmodel.UsuarioViewModel
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.ClienteViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EventosListScreen(
    onAgregarEventoClick: () -> Unit = {},
    onEditarEventoClick: (String) -> Unit = {},
    viewModel: SuperAdminViewModel = SuperAdminViewModel()
) {
    val eventos by viewModel.eventos.collectAsState()
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Título de la sección
        Text(
            text = "Gestión de Eventos",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botón de acción
        EventosButton(
            text = "Agregar Evento",
            onClick = onAgregarEventoClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título del listado
        Text(
            text = "Eventos Programados",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Listado elegante
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(eventos) { evento ->
                ElegantEventoItem(
                    evento = evento,
                    onEditarClick = onEditarEventoClick,
                    onItemClick = { eventoSeleccionado = evento }
                )
            }
        }
    }

    // Diálogo de detalles del evento
    eventoSeleccionado?.let { evento ->
        EventoDetallesDialog(
            evento = evento,
            onDismiss = { eventoSeleccionado = null },
            onEditar = {
                eventoSeleccionado = null
                onEditarEventoClick(evento.id)
            }
        )
    }
}

@Composable
fun ElegantEventoItem(
    evento: Evento,
    onEditarClick: (String) -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${evento.id}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandGold
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de editar
                    IconButton(
                        onClick = { onEditarClick(evento.id) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = BrandGold.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = BrandGold,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar evento",
                            tint = BrandGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Indicador de personas
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
                            text = "${evento.numeroPersonas} personas",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandGold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = evento.fecha,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Horario",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = "${evento.horaInicio} - ${evento.horaFin}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional
            Column {
                Text(
                    text = "Dirección",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = evento.direccionEvento,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Detalles",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = evento.detalleServicio,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun EventoDetallesDialog(
    evento: Evento,
    onDismiss: () -> Unit,
    onEditar: () -> Unit
) {
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val servicioViewModel: ServicioViewModel = viewModel()
    val mobiliarioViewModel: MobiliarioViewModel = viewModel()
    val clienteViewModel: ClienteViewModel = viewModel()
    val categoriaMobiliarioViewModel: CategoriaMobiliarioViewModel = viewModel()

    var empleados by remember { mutableStateOf(listOf<Usuario>()) }
    var servicio by remember { mutableStateOf<Servicio?>(null) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var categoriasMobiliario by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var clientesCargados by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        usuarioViewModel.obtenerUsuarios { listaEmpleados ->
            empleados = listaEmpleados.filter { it.id in evento.listaIdsEmpleados }
        }
        servicioViewModel.obtenerServicios { listaServicios ->
            servicio = listaServicios.find { it.id == evento.idServicio }
        }
        mobiliarioViewModel.obtenerMobiliario { listaMobiliarios ->
            val idsMobiliarios = evento.idMobiliario.split(",").filter { it.isNotEmpty() }
            mobiliarios = listaMobiliarios.filter { it.id in idsMobiliarios }
        }
        clienteViewModel.obtenerClientes { listaClientes ->
            println("DEBUG: Clientes cargados: ${listaClientes.size}")
            println("DEBUG: ID del cliente del evento: '${evento.idCliente}'")
            println("DEBUG: Lista de clientes disponibles:")
            listaClientes.forEach { cliente ->
                println("  - ID: '${cliente.id}', Nombre: '${cliente.nombre}', Teléfono: '${cliente.telefono}'")
            }
            cliente = listaClientes.find { it.id == evento.idCliente }
            println("DEBUG: Cliente encontrado: ${cliente?.nombre ?: "NO ENCONTRADO"}")
            if (cliente == null && evento.idCliente.isNotEmpty()) {
                println("DEBUG: ERROR - No se encontró el cliente con ID: '${evento.idCliente}'")
            }
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
                // Header con título y botones
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onEditar,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = BrandGold.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = BrandGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido con scroll
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Información básica
                    DetalleSeccion(
                        titulo = "Información General",
                        contenido = {
                            DetalleItem("ID del Evento", evento.id)
                            DetalleItem("Fecha", evento.fecha)
                            DetalleItem("Horario", "${evento.horaInicio} - ${evento.horaFin}")
                            DetalleItem("Número de Personas", "${evento.numeroPersonas} personas")
                            DetalleItem("Dirección del Evento", evento.direccionEvento)
                            DetalleItem("Comentarios", evento.detalleServicio)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Datos del Cliente
                    DetalleSeccion(
                        titulo = "Datos del Cliente",
                        contenido = {
                            if (!clientesCargados) {
                                DetalleItem("Estado", "Cargando datos del cliente...")
                            } else if (cliente != null) {
                                DetalleItem("Nombre", cliente!!.nombre)
                                DetalleItem("Teléfono", cliente!!.telefono)
                                DetalleItem("ID del Cliente", cliente!!.id)
                            } else {
                                DetalleItem("Cliente", "No encontrado")
                                DetalleItem("ID del Cliente", evento.idCliente.ifEmpty { "Sin asignar" })
                                DetalleItem("Estado", "Cliente no encontrado en la base de datos")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Servicio
                    DetalleSeccion(
                        titulo = "Servicio",
                        contenido = {
                            servicio?.let { serv ->
                                DetalleItem("Nombre", serv.nombre)
                                DetalleItem("Descripción", serv.descripcion)
                            } ?: DetalleItem("Servicio", "No encontrado")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Empleados
                    DetalleSeccion(
                        titulo = "Personal Asignado (${empleados.size} empleados)",
                        contenido = {
                            if (empleados.isNotEmpty()) {
                                empleados.forEach { empleado ->
                                    DetalleItem(
                                        "Empleado",
                                        "${empleado.nombre} ${empleado.apellidoPaterno} ${empleado.apellidoMaterno} - ${empleado.rol}"
                                    )
                                }
                            } else {
                                DetalleItem("Empleados", "Cargando...")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mobiliario
                    DetalleSeccion(
                        titulo = "Mobiliario Asignado (${mobiliarios.size} items)",
                        contenido = {
                            if (mobiliarios.isNotEmpty()) {
                                mobiliarios.forEach { mobiliario ->
                                    val categoria = categoriasMobiliario.find { it.id == mobiliario.idCategoria }
                                    val nombreCategoria = categoria?.nombre ?: "Sin categoría"
                                    DetalleItem(
                                        "Mobiliario",
                                        "$nombreCategoria - Color: ${mobiliario.color}"
                                    )
                                }
                            } else {
                                DetalleItem("Mobiliario", "Cargando...")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleSeccion(
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
fun DetalleItem(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun EventosButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = BrandGold.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = BrandGold,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrandGold
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ),
            maxLines = 2
        )
    }
}