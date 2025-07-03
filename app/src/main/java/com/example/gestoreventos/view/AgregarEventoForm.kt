package com.example.gestoreventos.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.*
import com.example.gestoreventos.viewmodel.*
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.gestoreventos.ui.theme.BrandGold
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEventoForm(
    eventoAEditar: Evento? = null,
    eventoViewModel: EventoViewModel = EventoViewModel(),
    clienteViewModel: ClienteViewModel = ClienteViewModel(),
    servicioViewModel: ServicioViewModel = ServicioViewModel(),
    mobiliarioViewModel: MobiliarioViewModel = MobiliarioViewModel(),
    usuarioViewModel: UsuarioViewModel = UsuarioViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var fecha by remember { mutableStateOf(eventoAEditar?.fecha ?: "") }
    var horaInicio by remember { mutableStateOf(eventoAEditar?.horaInicio ?: "") }
    var horaFin by remember { mutableStateOf(eventoAEditar?.horaFin ?: "") }
    var numeroPersonas by remember { mutableStateOf(eventoAEditar?.numeroPersonas?.toString() ?: "") }
    var detalleServicio by remember { mutableStateOf(eventoAEditar?.detalleServicio ?: "") }
    var direccionEvento by remember { mutableStateOf(eventoAEditar?.direccionEvento ?: "") }

    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }
    var mobiliariosSeleccionados = remember { mutableStateListOf<Mobiliario>() }
    var empleadosSeleccionados = remember { mutableStateListOf<Usuario>() }

    var servicios by remember { mutableStateOf(listOf<Servicio>()) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var categoriasMobiliario by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var usuarios by remember { mutableStateOf(listOf<Usuario>()) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }

    // Variables para datos del cliente
    var nombreCliente by remember { mutableStateOf("") }
    var telefonoCliente by remember { mutableStateOf("") }

    var serviciosSeleccionados = remember { mutableStateListOf<Servicio>() }

    val empleadosActivos = usuarios.filter { it.estado == "activo" }
    val mobiliariosActivos = mobiliarios.filter { it.estado == "activo" }
    val serviciosActivos = servicios.filter { it.estado == "activo" }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var precargado by remember { mutableStateOf(false) }
    LaunchedEffect(servicios, mobiliarios, usuarios, clientes, eventoAEditar) {
        if (eventoAEditar != null && !precargado) {
            // Precargar fecha, hora, etc.
            fecha = eventoAEditar.fecha
            horaInicio = eventoAEditar.horaInicio
            horaFin = eventoAEditar.horaFin
            numeroPersonas = eventoAEditar.numeroPersonas.toString()
            direccionEvento = eventoAEditar.direccionEvento
            detalleServicio = eventoAEditar.detalleServicio

            // Precargar servicios seleccionados (multi)
            val idsServicios = eventoAEditar.idServicio.split(",").filter { it.isNotEmpty() }
            serviciosSeleccionados.clear()
            serviciosSeleccionados.addAll(servicios.filter { it.id in idsServicios })

            // Precargar mobiliarios seleccionados
            val idsMobiliarios = eventoAEditar.idMobiliario.split(",").filter { it.isNotEmpty() }
            mobiliariosSeleccionados.clear()
            mobiliariosSeleccionados.addAll(
                mobiliarios.filter { it.id in idsMobiliarios }
            )

            // Precargar empleados seleccionados
            empleadosSeleccionados.clear()
            empleadosSeleccionados.addAll(
                usuarios.filter { it.id in eventoAEditar.listaIdsEmpleados }
            )

            // Precargar datos del cliente
            val clienteEvento = clientes.find { it.id == eventoAEditar.idCliente }
            if (clienteEvento != null) {
                nombreCliente = clienteEvento.nombre
                telefonoCliente = clienteEvento.telefono
            }
            precargado = true
        }
    }

    LaunchedEffect(Unit) {
        servicioViewModel.obtenerServicios { servicios = it }
        mobiliarioViewModel.obtenerMobiliario { mobiliarios = it }
        mobiliarioViewModel.obtenerCategoriasMobiliario { categoriasMobiliario = it }
        usuarioViewModel.obtenerUsuarios { usuarios = it }
        clienteViewModel.obtenerClientes { clientes = it }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = BrandGold,
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    content = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = data.visuals.message,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White
                                )
                            )
                        }
                    }
                )
            }
        }
        Text(
            text = if (eventoAEditar != null) "Editar Evento" else "Agregar Evento",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha - Corregido para que funcione el click
        OutlinedTextField(
            value = fecha,
            onValueChange = { },
            readOnly = true,
            label = { Text("Fecha del evento") },
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.clickable {
                        showDatePicker(context) { selected -> fecha = selected }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker(context) { selected -> fecha = selected }
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hora inicio - Corregido
        OutlinedTextField(
            value = horaInicio,
            onValueChange = { },
            readOnly = true,
            label = { Text("Hora Inicio") },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar hora",
                    modifier = Modifier.clickable {
                        showTimePicker(context) { selected -> horaInicio = selected }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(context) { selected -> horaInicio = selected }
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hora fin - Corregido
        OutlinedTextField(
            value = horaFin,
            onValueChange = { },
            readOnly = true,
            label = { Text("Hora Fin") },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar hora",
                    modifier = Modifier.clickable {
                        showTimePicker(context) { selected -> horaFin = selected }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(context) { selected -> horaFin = selected }
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = numeroPersonas,
            onValueChange = { numeroPersonas = it.filter { c -> c.isDigit() } },
            label = { Text("Número de personas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario de Cliente embebido
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
        Text("Datos del Cliente", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombreCliente,
            onValueChange = { nombreCliente = it },
            label = { Text("Nombre del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Teléfono limitado a 10 dígitos
        OutlinedTextField(
            value = telefonoCliente,
            onValueChange = {
                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                    telefonoCliente = it
                }
            },
            label = { Text("Teléfono del Cliente (10 dígitos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("${telefonoCliente.length}/10") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Servicio - Multi-selección
        Text("Servicios", style = MaterialTheme.typography.titleMedium)
        if (serviciosActivos.isNotEmpty()) {
            Column {
                serviciosActivos.forEach { servicio ->
                    val isSelected = serviciosSeleccionados.contains(servicio)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = isSelected,
                                onValueChange = { checked ->
                                    if (checked) serviciosSeleccionados.add(servicio)
                                    else serviciosSeleccionados.remove(servicio)
                                }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(servicio.nombre)
                    }
                }
            }
        } else {
            Text("Cargando servicios...", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mobiliario - Selección múltiple con barras de categorías
        Text("Mobiliario Asignado", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        if (categoriasMobiliario.isNotEmpty() && mobiliariosActivos.isNotEmpty()) {
            MobiliarioCategoriaSelector(
                categorias = categoriasMobiliario,
                mobiliarios = mobiliariosActivos,
                mobiliariosSeleccionados = mobiliariosSeleccionados,
                onMobiliarioSeleccionado = { mobiliario, seleccionado ->
                    if (seleccionado) {
                        mobiliariosSeleccionados.add(mobiliario)
                    } else {
                        mobiliariosSeleccionados.remove(mobiliario)
                    }
                }
            )
        } else {
            Text("Cargando mobiliario...", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Empleados
        Text("Empleados Asignados", style = MaterialTheme.typography.titleMedium)
        if (empleadosActivos.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(empleadosActivos.size) { index ->
                    val emp = empleadosActivos[index]
                    val isSelected = empleadosSeleccionados.contains(emp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = isSelected,
                                onValueChange = { isChecked ->
                                    if (isChecked) {
                                        empleadosSeleccionados.add(emp)
                                    } else {
                                        empleadosSeleccionados.remove(emp)
                                    }
                                }
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${emp.nombre} ${emp.apellidoPaterno}")
                    }
                }
            }
        } else {
            Text("Cargando empleados...", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direccionEvento,
            onValueChange = { direccionEvento = it },
            label = { Text("Dirección del evento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = detalleServicio,
            onValueChange = { detalleServicio = it },
            label = { Text("Detalles / observaciones") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validaciones
                if (fecha.isBlank() || horaInicio.isBlank() || horaFin.isBlank() ||
                    numeroPersonas.isBlank() || nombreCliente.isBlank() ||
                    telefonoCliente.length != 10 ||
                    serviciosSeleccionados.isEmpty() || mobiliariosSeleccionados.isEmpty() ||
                    empleadosSeleccionados.isEmpty()
                ) {
                    // Aquí podrías mostrar un mensaje de error
                    return@Button
                }
                val idsServicios = serviciosSeleccionados.joinToString(",") { it.id }
                if (eventoAEditar != null) {
                    // Actualizar evento existente
                    val clienteExistente = clientes.find { it.nombre == nombreCliente.trim() && it.telefono == telefonoCliente.trim() }
                    val idClienteFinal = clienteExistente?.id ?: eventoAEditar.idCliente
                    val eventoActualizado = eventoAEditar.copy(
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        numeroPersonas = numeroPersonas.toInt(),
                        direccionEvento = direccionEvento,
                        listaIdsEmpleados = empleadosSeleccionados.map { it.id },
                        idMobiliario = mobiliariosSeleccionados.joinToString(",") { it.id },
                        idServicio = idsServicios,
                        detalleServicio = detalleServicio,
                        idCliente = idClienteFinal
                    )
                    eventoViewModel.actualizarEvento(
                        eventoActualizado,
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Evento actualizado")
                            }
                        },
                        onFailure = {
                            // Manejar error
                        }
                    )
                } else {
                    // Crear nuevo evento
                    val clienteExistente = clientes.find { it.nombre == nombreCliente.trim() && it.telefono == telefonoCliente.trim() }
                    if (clienteExistente != null) {
                        // Si el cliente ya existe, usa su ID
                        eventoViewModel.agregarEventoAutoId(
                            fecha = fecha,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            numeroPersonas = numeroPersonas.toInt(),
                            idCliente = clienteExistente.id,
                            direccionEvento = direccionEvento,
                            listaIdsEmpleados = empleadosSeleccionados.map { it.id },
                            idMobiliario = mobiliariosSeleccionados.joinToString(",") { it.id },
                            idServicio = idsServicios,
                            detalleServicio = detalleServicio,
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Evento agregado correctamente")
                                }
                                // Limpiar formulario
                                fecha = ""
                                horaInicio = ""
                                horaFin = ""
                                numeroPersonas = ""
                                nombreCliente = ""
                                telefonoCliente = ""
                                serviciosSeleccionados.clear()
                                mobiliariosSeleccionados.clear()
                                empleadosSeleccionados.clear()
                                direccionEvento = ""
                                detalleServicio = ""
                            },
                            onFailure = {
                                // Manejar error
                            }
                        )
                    } else {
                        // Si el cliente no existe, créalo y luego usa su ID
                        val clienteViewModelLocal = ClienteViewModel()
                        clienteViewModelLocal.agregarCliente(
                            nombreCliente.trim(),
                            telefonoCliente.trim(),
                            onSuccess = { clienteId ->
                                eventoViewModel.agregarEventoAutoId(
                                    fecha = fecha,
                                    horaInicio = horaInicio,
                                    horaFin = horaFin,
                                    numeroPersonas = numeroPersonas.toInt(),
                                    idCliente = clienteId,
                                    direccionEvento = direccionEvento,
                                    listaIdsEmpleados = empleadosSeleccionados.map { it.id },
                                    idMobiliario = mobiliariosSeleccionados.joinToString(",") { it.id },
                                    idServicio = idsServicios,
                                    detalleServicio = detalleServicio,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Evento agregado correctamente")
                                        }
                                        // Limpiar formulario
                                        fecha = ""
                                        horaInicio = ""
                                        horaFin = ""
                                        numeroPersonas = ""
                                        nombreCliente = ""
                                        telefonoCliente = ""
                                        serviciosSeleccionados.clear()
                                        mobiliariosSeleccionados.clear()
                                        empleadosSeleccionados.clear()
                                        direccionEvento = ""
                                        detalleServicio = ""
                                    },
                                    onFailure = {
                                        // Manejar error
                                    }
                                )
                            },
                            onFailure = {
                                // Manejar error
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (eventoAEditar != null) "Actualizar Evento" else "Guardar Evento")
        }
    }
}

@Composable
fun <T> DropdownSelector(
    label: String,
    seleccionado: String?,
    opciones: List<Pair<String, T>>,
    onSeleccionado: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = seleccionado ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Expandir lista",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { (text, value) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSeleccionado(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val fechaFormateada = String.format("%02d/%02d/%04d", day, month + 1, year)
            onDateSelected(fechaFormateada)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.setTitle("Selecciona una fecha")
    datePicker.show()
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val hourFormatted = if (hour % 12 == 0) 12 else hour % 12
            val timeFormatted = String.format("%02d:%02d %s", hourFormatted, minute, amPm)
            onTimeSelected(timeFormatted)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // false para formato 12 horas
    ).show()
}

@Composable
fun MobiliarioCategoriaSelector(
    categorias: List<CategoriaMobiliario>,
    mobiliarios: List<Mobiliario>,
    mobiliariosSeleccionados: List<Mobiliario>,
    onMobiliarioSeleccionado: (Mobiliario, Boolean) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaMobiliario?>(null) }

    // Barra de categorías
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categorias.size) { index ->
            val categoria = categorias[index]
            val isSelected = categoriaSeleccionada?.id == categoria.id
            val mobiliariosEnCategoria = mobiliarios.filter { it.idCategoria == categoria.id }
            val seleccionadosEnCategoria = mobiliariosEnCategoria.count { it in mobiliariosSeleccionados }

            Card(
                modifier = Modifier
                    .clickable {
                        categoriaSeleccionada = if (isSelected) null else categoria
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFd4af37) else Color(0xFF2c2c2c)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = categoria.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.Black else Color.White
                    )
                    if (seleccionadosEnCategoria > 0) {
                        Text(
                            text = "$seleccionadosEnCategoria seleccionado(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    // Opciones de mobiliario de la categoría seleccionada
    categoriaSeleccionada?.let { categoria ->
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Opciones de ${categoria.nombre}",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFFd4af37)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val mobiliariosEnCategoria = mobiliarios.filter { it.idCategoria == categoria.id }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            items(mobiliariosEnCategoria.size) { index ->
                val mobiliario = mobiliariosEnCategoria[index]
                val isSelected = mobiliariosSeleccionados.contains(mobiliario)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onMobiliarioSeleccionado(mobiliario, !isSelected)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFd4af37).copy(alpha = 0.2f) else Color(0xFF2c2c2c)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                onMobiliarioSeleccionado(mobiliario, checked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFd4af37),
                                uncheckedColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = mobiliario.color,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = "ID: ${mobiliario.id}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}