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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
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
import com.example.gestoreventos.utils.PdfGenerator
import com.example.gestoreventos.utils.DateUtils
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import java.util.*

@Composable
fun EventosListScreen(
    onAgregarEventoClick: () -> Unit = {},
    onEditarEventoClick: (String) -> Unit = {},
    onCalendarioClick: () -> Unit = {},
    onChecklistClick: (String) -> Unit = {}, // NUEVO: Callback para abrir checklist
    currentUser: Usuario? = null,
    viewModel: SuperAdminViewModel = SuperAdminViewModel()
) {
    val eventos by viewModel.eventos.collectAsState()

    // Cargar eventos cuando se inicie la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    // Estado para el filtro
    var filtroExpandido by remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf("Todos los Eventos") }
    val currentUserId = currentUser?.id

    // Filtrado y ordenamiento estable usando derivedStateOf
    val eventosFiltrados by remember {
        derivedStateOf {
            if (eventos.isEmpty()) {
                emptyList()
            } else {
                val eventosFiltro = when (filtroSeleccionado) {
                    "Mis Eventos" -> {
                        if (currentUserId != null) {
                            eventos.filter { evento ->
                                !DateUtils.isEventoPasado(evento) &&
                                        evento.listaIdsEmpleados.contains(currentUserId)
                            }
                        } else {
                            emptyList()
                        }
                    }
                    "Eventos Pasados" -> eventos.filter { DateUtils.isEventoPasado(it) }
                    else -> eventos.filter { !DateUtils.isEventoPasado(it) }
                }

                // NUEVO: Ordenar por fecha - de más próximo a más lejano
                eventosFiltro.sortedBy { evento ->
                    DateUtils.parseFechaParaOrdenar(evento.fecha)
                }
            }
        }
    }

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

        // Botones de acción según rol
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón de agregar evento (solo para admin y super admin)
            if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
                EventosButton(
                    text = "Agregar Evento",
                    onClick = onAgregarEventoClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón de calendario (para todos los roles)
            EventosButton(
                text = "Calendario",
                onClick = onCalendarioClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro de eventos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Filtrar Eventos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandGold
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Dropdown del filtro
                Box {
                    OutlinedButton(
                        onClick = { filtroExpandido = !filtroExpandido },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, BrandGold)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = filtroSeleccionado,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = BrandGold,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                imageVector = if (filtroExpandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir filtro",
                                tint = BrandGold
                            )
                        }
                    }

                    // Opciones del dropdown
                    if (filtroExpandido) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column {
                                listOf("Todos los Eventos", "Mis Eventos", "Eventos Pasados").forEach { opcion ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (filtroSeleccionado != opcion) {
                                                    filtroSeleccionado = opcion
                                                }
                                                filtroExpandido = false
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = opcion,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (filtroSeleccionado == opcion) BrandGold else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (filtroSeleccionado == opcion) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título del listado dinámico
        Text(
            text = when (filtroSeleccionado) {
                "Eventos Pasados" -> "Eventos Pasados"
                else -> "Eventos Programados"
            },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Listado elegante sin indicadores de carga
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = eventosFiltrados,
                key = { evento -> evento.id }
            ) { evento ->
                ElegantEventoItem(
                    evento = evento,
                    onEditarClick = onEditarEventoClick,
                    onItemClick = { eventoSeleccionado = evento },
                    onChecklistClick = onChecklistClick, // NUEVO
                    currentUser = currentUser,
                    isEventoPasado = DateUtils.isEventoPasado(evento)
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
            },
            currentUser = currentUser
        )
    }
}

@Composable
fun ElegantEventoItem(
    evento: Evento,
    onEditarClick: (String) -> Unit,
    onItemClick: () -> Unit,
    onChecklistClick: (String) -> Unit, // NUEVO
    currentUser: Usuario? = null,
    isEventoPasado: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isEventoPasado)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                else
                    BrandGold.copy(alpha = 0.2f)
            )
            .border(
                width = 1.dp,
                color = if (isEventoPasado)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                else
                    BrandGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEventoPasado)
                MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surface
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ID: ${evento.id}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isEventoPasado)
                                MaterialTheme.colorScheme.error
                            else
                                BrandGold
                        )
                    )

                    // Indicador de evento pasado
                    if (isEventoPasado) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "PASADO",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de editar (solo para admin y super admin, y solo para eventos futuros)
                    if ((currentUser?.rol == "admin" || currentUser?.rol == "super_admin") && !isEventoPasado) {
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
                    }

                    // Indicador de personas
                    Card(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (isEventoPasado)
                                    MaterialTheme.colorScheme.error
                                else
                                    BrandGold,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEventoPasado)
                                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            else
                                BrandGold.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${evento.numeroPersonas} personas",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isEventoPasado)
                                    MaterialTheme.colorScheme.error
                                else
                                    BrandGold
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
                            fontWeight = FontWeight.Medium,
                            color = if (isEventoPasado)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface
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
                            fontWeight = FontWeight.Medium,
                            color = if (isEventoPasado)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface
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
    onEditar: () -> Unit,
    currentUser: Usuario? = null
) {
    val context = LocalContext.current
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

    // Calcular saldo pendiente
    val saldoPendiente = evento.precioTotal - evento.anticipo
    val estaLiquidado = saldoPendiente <= 0.0

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
                        // Botón de editar (solo para admin y super admin)
                        if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
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
                            DetalleItem("ID evento", evento.id)
                            DetalleItem("Fecha", evento.fecha)
                            DetalleItem("Horario", "${evento.horaInicio} - ${evento.horaFin}")
                            DetalleItem("No. Personas", "${evento.numeroPersonas} personas")
                            DetalleItem("Dirección", evento.direccionEvento)
                            DetalleItem("Notas", evento.detalleServicio)
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
                                DetalleItem("ID cliente", cliente!!.id)
                            } else {
                                DetalleItem("Cliente", "No encontrado")
                                DetalleItem("ID cliente", evento.idCliente.ifEmpty { "Sin asignar" })
                                DetalleItem("Estado", "Cliente no encontrado en la base de datos")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // NUEVA SECCIÓN: Información Financiera
                    DetalleSeccion(
                        titulo = "Información Financiera",
                        contenido = {
                            DetalleItem("Precio Total", "$${String.format("%.2f", evento.precioTotal)}")
                            DetalleItem("Anticipo", "$${String.format("%.2f", evento.anticipo)}")

                            // Saldo Pendiente o Liquidado
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (estaLiquidado) "Estado:" else "Saldo Pendiente:",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    ),
                                    modifier = Modifier.weight(0.4f)
                                )

                                if (estaLiquidado) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "LIQUIDADO",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4CAF50)
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "$${String.format("%.2f", saldoPendiente)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        ),
                                        modifier = Modifier.weight(0.6f)
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Servicios con categorías, opciones y CANTIDAD
                    if (evento.serviciosSeleccionados.isNotEmpty()) {
                        DetalleSeccion(
                            titulo = "Servicios",
                            contenido = {
                                evento.serviciosSeleccionados.forEach { servicioSeleccionado ->
                                    // Buscar el servicio para obtener su nombre
                                    val nombreServicio = todosLosServicios.find { it.id == servicioSeleccionado.idServicio }?.nombre
                                        ?: "Servicio ${servicioSeleccionado.idServicio}"

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        // Nombre del servicio y cantidad
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "• $nombreServicio",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )

                                            // NUEVO: Mostrar cantidad
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = BrandGold.copy(alpha = 0.15f)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${servicioSeleccionado.cantidad} pz",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = BrandGold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        servicioSeleccionado.categoriasSeleccionadas.forEach { categoria ->
                                            val opcionesTexto = categoria.opcionesSeleccionadas.joinToString(", ")
                                            if (opcionesTexto.isNotEmpty()) {
                                                Text(
                                                    text = "  - ${categoria.nombreCategoria}: $opcionesTexto",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Normal,
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                                    ),
                                                    modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

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

                    // Botones de PDF (solo para admin y super admin)
                    if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botón PDF para Cliente
                            EventosButton(
                                text = "PDF Cliente",
                                onClick = {
                                    val pdfUri = PdfGenerator.generateClientPdf(
                                        context = context,
                                        evento = evento,
                                        cliente = cliente,
                                        servicio = servicio,
                                        empleados = empleados,
                                        mobiliarios = mobiliarios,
                                        todosLosServicios = todosLosServicios,
                                        categoriasMobiliario = categoriasMobiliario
                                    )
                                    pdfUri?.let { uri ->
                                        val fileName = "Caruma_Cliente_Evento_${evento.id}.pdf"
                                        PdfGenerator.sharePdf(context, uri, fileName)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Botón PDF para Trabajadores
                            EventosButton(
                                text = "PDF Trabajadores",
                                onClick = {
                                    val pdfUri = PdfGenerator.generateWorkerPdf(
                                        context = context,
                                        evento = evento,
                                        cliente = cliente,
                                        servicio = servicio,
                                        empleados = empleados,
                                        mobiliarios = mobiliarios,
                                        todosLosServicios = todosLosServicios,
                                        categoriasMobiliario = categoriasMobiliario
                                    )
                                    pdfUri?.let { uri ->
                                        val fileName = "Caruma_Trabajadores_Evento_${evento.id}.pdf"
                                        PdfGenerator.sharePdf(context, uri, fileName)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
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