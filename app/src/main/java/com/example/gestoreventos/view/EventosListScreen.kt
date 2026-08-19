package com.example.gestoreventos.view

import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.model.Pago
import com.example.gestoreventos.viewmodel.SuperAdminViewModel
import com.example.gestoreventos.viewmodel.UsuarioViewModel
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.ClienteViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel
import com.example.gestoreventos.viewmodel.PagoViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.SuccessGreen
import com.example.gestoreventos.ui.theme.SuccessGreenBg
import com.example.gestoreventos.ui.theme.WarningGold
import com.example.gestoreventos.ui.theme.WarningGoldBg
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.ErrorRedBg
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.utils.PdfGenerator
import com.example.gestoreventos.utils.DateUtils
import android.content.Context
import android.net.Uri
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
    onChecklistClick: (String) -> Unit,
    currentUser: Usuario? = null,
    isEventoPasado: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = if (isEventoPasado) ErrorRedBg.copy(alpha = 0.6f) else CardBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEventoPasado) ErrorRedBg.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // HEADER: ID y badge de personas
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
                                containerColor = ErrorRedBg
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "PASADO",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Indicador de personas
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEventoPasado) ErrorRedBg else WarningGoldBg
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${evento.numeroPersonas} personas",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isEventoPasado) ErrorRed else WarningGold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INFORMACIÓN PRINCIPAL: Fecha y Horario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }

                Column(horizontalAlignment = Alignment.End) {
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

            // DIRECCIÓN (sin detalles)
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
                    ),
                    maxLines = 2 // Limitar a 2 líneas
                )
            }

            // BOTONES ABAJO (solo para eventos futuros)
            if (!isEventoPasado) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón de Checklist (siempre visible para eventos futuros)
                    Button(
                        onClick = { onChecklistClick(evento.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreenBg,
                            contentColor = SuccessGreen
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Checklist",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Checklist",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                    }

                    // Botón de Editar (solo para admin y super admin)
                    if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
                        Button(
                            onClick = { onEditarClick(evento.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGold,
                                contentColor = BrandBlack
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Editar",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
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
    val pagoViewModel: PagoViewModel = viewModel()

    var empleados by remember { mutableStateOf(listOf<Usuario>()) }
    var servicio by remember { mutableStateOf<Servicio?>(null) }
    var todosLosServicios by remember { mutableStateOf(listOf<Servicio>()) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var categoriasMobiliario by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var clientesCargados by remember { mutableStateOf(false) }
    var mostrarDialogoPdf by remember { mutableStateOf(false) }
    var pdfUriGenerado by remember { mutableStateOf<Uri?>(null) }
    var pdfNombreGenerado by remember { mutableStateOf("") }
    var pagos by remember { mutableStateOf(listOf<Pago>()) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }

    fun recargarPagos() {
        pagoViewModel.obtenerPagosDeEvento(evento.id) { lista ->
            pagos = lista.sortedBy { it.fecha }
        }
    }

    // Saldo pendiente calculado en vivo sumando todos los Pago de este evento —
    // ya no depende de editar el campo "anticipo" a mano.
    val totalPagado = pagos.sumOf { it.monto }
    val saldoPendiente = evento.precioTotal - totalPagado
    val estaLiquidado = saldoPendiente <= 0.0

    LaunchedEffect(Unit) {
        recargarPagos()
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
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

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
                SelectionContainer {
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = if (estaLiquidado) "ESTADO" else "SALDO PENDIENTE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGold.copy(alpha = 0.7f),
                                            letterSpacing = 0.5.sp
                                        )
                                    )

                                    if (estaLiquidado) {
                                        Card(
                                            modifier = Modifier.padding(top = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = SuccessGreenBg
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "LIQUIDADO",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessGreen
                                                ),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "$${String.format("%.2f", saldoPendiente)}",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
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
                                                        containerColor = WarningGoldBg
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = "${servicioSeleccionado.cantidad} pz",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = WarningGold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            servicioSeleccionado.categoriasSeleccionadas.forEach { categoria ->
                                                val opcionesTexto = categoria.opcionesSeleccionadas.joinToString(", ")
                                                if (opcionesTexto.isNotEmpty()) {
                                                    Text(
                                                        text = "- ${categoria.nombreCategoria}: $opcionesTexto",
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Historial de Pagos - suma en vivo lo que alimenta el Saldo Pendiente de arriba
                        DetalleSeccion(
                            titulo = "Historial de Pagos",
                            contenido = {
                                if (pagos.isEmpty()) {
                                    Text(
                                        text = "Sin pagos registrados todavía",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    )
                                } else {
                                    pagos.forEach { pago ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = pago.tipo.replaceFirstChar { it.uppercase() },
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                                Text(
                                                    text = pago.fecha,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                    )
                                                )
                                            }
                                            Text(
                                                text = "+$${String.format(java.util.Locale.getDefault(), "%.2f", pago.monto)}",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessGreen
                                                )
                                            )
                                        }
                                    }
                                }

                                if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { mostrarDialogoPago = true }) {
                                        Text("+ Registrar Pago", color = BrandGold, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        )

                        // Botón de PDF del contrato (solo para admin y super admin)
                        // NOTA: se quitó el botón "PDF Trabajadores" — con datos/wifi disponibles en el
                        // evento, los empleados ya consultan todo desde su perfil en la app (checklist
                        // incluido), así que ese PDF quedaba duplicando información que ya vive en la app.
                        if (currentUser?.rol == "admin" || currentUser?.rol == "super_admin") {
                            Spacer(modifier = Modifier.height(16.dp))

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

                                    if (pdfUri != null) {
                                        pdfUriGenerado = pdfUri
                                        pdfNombreGenerado = "Contrato_${formatearFechaParaNombre(evento.fecha)}.pdf"
                                        mostrarDialogoPdf = true
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Error al generar el PDF",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
    if (mostrarDialogoPdf) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPdf = false },
            title = {
                Text(
                    "PDF generado correctamente",
                    style = MaterialTheme.typography.titleMedium.copy(color = BrandGold)
                )
            },
            text = { Text("¿Qué deseas hacer con el PDF?") },
            confirmButton = {
                TextButton(onClick = {
                    pdfUriGenerado?.let { PdfGenerator.sharePdf(context, it, pdfNombreGenerado) }
                    mostrarDialogoPdf = false
                }) {
                    Text("Compartir", color = BrandGold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        pdfUriGenerado?.let { uri ->
                            val descargado = PdfGenerator.savePdfToDownloads(context, uri, pdfNombreGenerado)
                            if (descargado) {
                                android.widget.Toast.makeText(
                                    context,
                                    "PDF guardado en Descargas: $pdfNombreGenerado",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        mostrarDialogoPdf = false
                    }) {
                        Text("Descargar")
                    }
                    TextButton(onClick = { mostrarDialogoPdf = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    if (mostrarDialogoPago) {
        RegistrarPagoDialog(
            pagoViewModel = pagoViewModel,
            idEvento = evento.id,
            onDismiss = { mostrarDialogoPago = false },
            onPagoRegistrado = {
                mostrarDialogoPago = false
                recargarPagos()
            }
        )
    }
}

@Composable
fun RegistrarPagoDialog(
    pagoViewModel: PagoViewModel,
    idEvento: String,
    onDismiss: () -> Unit,
    onPagoRegistrado: () -> Unit
) {
    val context = LocalContext.current
    var montoTexto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("liquidacion") }
    var fecha by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    val opcionesTipo = listOf("liquidacion" to "Liquidación", "abono" to "Abono")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Registrar Pago",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandGold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TIPO DE PAGO",
                    style = MaterialTheme.typography.labelSmall.copy(color = BrandGold.copy(alpha = 0.7f))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcionesTipo.forEach { (valor, etiqueta) ->
                        val seleccionado = tipo == valor
                        Card(
                            modifier = Modifier
                                .clickable { tipo = valor }
                                .border(
                                    width = 1.dp,
                                    color = if (seleccionado) BrandGold else CardBorder,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (seleccionado) WarningGoldBg else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = etiqueta,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (seleccionado) WarningGold else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = {
                        val filtrado = it.filter { c -> c.isDigit() || c == '.' }
                        if (filtrado.count { c -> c == '.' } <= 1) montoTexto = filtrado
                    },
                    label = { Text("Monto") },
                    prefix = { Text("$") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGold,
                        focusedLabelColor = BrandGold,
                        cursorColor = BrandGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha del pago") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = BrandGold,
                            modifier = Modifier.clickable {
                                showDatePicker(context) { seleccionada -> fecha = seleccionada }
                            }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGold,
                        focusedLabelColor = BrandGold,
                        cursorColor = BrandGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker(context) { seleccionada -> fecha = seleccionada } }
                )

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val monto = montoTexto.toDoubleOrNull()
                            if (monto == null || monto <= 0.0 || fecha.isBlank()) {
                                error = "Completa monto y fecha"
                                return@Button
                            }
                            guardando = true
                            val partes = fecha.split("/")
                            val mes = "${partes.getOrNull(2)}-${partes.getOrNull(1)?.padStart(2, '0')}"
                            pagoViewModel.agregarPago(
                                idEvento = idEvento,
                                monto = monto,
                                tipo = tipo,
                                fecha = fecha,
                                mes = mes,
                                onSuccess = {
                                    guardando = false
                                    onPagoRegistrado()
                                },
                                onFailure = {
                                    guardando = false
                                    error = "Error al guardar: ${it.message}"
                                }
                            )
                        },
                        enabled = !guardando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGold,
                            contentColor = BrandBlack
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (guardando) "Guardando..." else "Guardar")
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
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(
                width = 1.dp,
                color = CardBorder,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = etiqueta.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
            .height(56.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = CardBorder,
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

// Agregar esta función helper al final del archivo, fuera de los composables
private fun formatearFechaParaNombre(fecha: String): String {
    return try {
        val meses = arrayOf(
            "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val partes = fecha.split("/")
        val dia = partes[0]
        val mes = meses[partes[1].toInt()]
        val anio = partes[2]
        "${dia}${mes}${anio}"
    } catch (e: Exception) {
        "Evento"
    }
}