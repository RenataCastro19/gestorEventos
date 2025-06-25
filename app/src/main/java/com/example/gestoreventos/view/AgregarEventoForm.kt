package com.example.gestoreventos.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.*
import com.example.gestoreventos.viewmodel.*
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ArrowDropDown


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEventoForm(
    eventoViewModel: EventoViewModel = EventoViewModel(),
    clienteViewModel: ClienteViewModel = ClienteViewModel(),
    servicioViewModel: ServicioViewModel = ServicioViewModel(),
    mobiliarioViewModel: MobiliarioViewModel = MobiliarioViewModel(),
    usuarioViewModel: UsuarioViewModel = UsuarioViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var fecha by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var numeroPersonas by remember { mutableStateOf("") }
    var detalleServicio by remember { mutableStateOf("") }
    var direccionEvento by remember { mutableStateOf("") }

    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }
    var mobiliariosSeleccionados = remember { mutableStateListOf<Mobiliario>() }
    var empleadosSeleccionados = remember { mutableStateListOf<Usuario>() }

    var servicios by remember { mutableStateOf(listOf<Servicio>()) }
    var mobiliarios by remember { mutableStateOf(listOf<Mobiliario>()) }
    var usuarios by remember { mutableStateOf(listOf<Usuario>()) }

    LaunchedEffect(Unit) {
        servicioViewModel.obtenerServicios { servicios = it }
        mobiliarioViewModel.obtenerMobiliario { mobiliarios = it }
        usuarioViewModel.obtenerUsuarios { usuarios = it }

    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Agregar Evento", style = MaterialTheme.typography.titleLarge)

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

        var nombreCliente by remember { mutableStateOf("") }
        var telefonoCliente by remember { mutableStateOf("") }

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

        // Servicio - Corregido
        Text("Servicio", style = MaterialTheme.typography.titleMedium)
        DropdownSelector(
            label = "Selecciona un servicio",
            seleccionado = servicioSeleccionado?.nombre,
            opciones = servicios.map { it.nombre to it },
            onSeleccionado = { servicioSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mobiliario - Selección múltiple
        Text("Mobiliario Asignado", style = MaterialTheme.typography.titleMedium)
        if (mobiliarios.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(mobiliarios.size) { index ->
                    val mob = mobiliarios[index]
                    val isSelected = mobiliariosSeleccionados.contains(mob)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = isSelected,
                                onValueChange = { isChecked ->
                                    if (isChecked) {
                                        mobiliariosSeleccionados.add(mob)
                                    } else {
                                        mobiliariosSeleccionados.remove(mob)
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
                        Text("${mob.color} (ID: ${mob.id})")
                    }
                }
            }
        } else {
            Text("Cargando mobiliario...", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Empleados
        Text("Empleados Asignados", style = MaterialTheme.typography.titleMedium)
        if (usuarios.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(usuarios.size) { index ->
                    val emp = usuarios[index]
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
                    servicioSeleccionado == null || mobiliariosSeleccionados.isEmpty() ||
                    empleadosSeleccionados.isEmpty()
                ) {
                    // Aquí podrías mostrar un mensaje de error
                    return@Button
                }

                val clienteViewModelLocal = ClienteViewModel()
                clienteViewModelLocal.agregarCliente(
                    nombreCliente.trim(),
                    telefonoCliente.trim(),
                    onSuccess = {
                        val nuevoCliente = Cliente("", nombreCliente.trim(), telefonoCliente.trim())
                        eventoViewModel.agregarEventoAutoId(
                            fecha = fecha,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            numeroPersonas = numeroPersonas.toInt(),
                            idCliente = nuevoCliente.id,
                            direccionEvento = direccionEvento,
                            listaIdsEmpleados = empleadosSeleccionados.map { it.id },
                            idMobiliario = mobiliariosSeleccionados.joinToString(",") { it.id },
                            idServicio = servicioSeleccionado!!.id,
                            detalleServicio = detalleServicio,
                            onSuccess = {
                                // Limpiar formulario o navegar
                            },
                            onFailure = {
                                // Manejar error
                            }
                        )
                    },
                    onFailure = {
                        // Manejar error al crear cliente
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Evento")
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
            val fechaFormateada = String.format("%04d-%02d-%02d", year, month + 1, day)
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