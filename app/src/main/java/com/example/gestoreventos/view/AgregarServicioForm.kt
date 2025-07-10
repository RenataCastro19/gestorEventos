package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.CategoriaServicio
import com.example.gestoreventos.viewmodel.ServicioViewModel

@Composable
fun AgregarServicioForm(
    modifier: Modifier = Modifier,
    viewModel: ServicioViewModel = ServicioViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var categoriasConfirmadas by remember { mutableStateOf(mutableListOf<CategoriaServicio>()) }
    var mensaje by remember { mutableStateOf("") }

    // Estados para el formulario de categoría actual
    var mostrarFormularioCategoria by remember { mutableStateOf(false) }
    var nombreCategoriaActual by remember { mutableStateOf("") }
    var nuevaOpcion by remember { mutableStateOf("") }
    var opcionesActuales by remember { mutableStateOf(mutableListOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Agregar Servicio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Información básica del servicio
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Información del Servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del servicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = precioTexto,
                    onValueChange = { precioTexto = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Precio por unidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de categorías
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Categorías y Opciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón para mostrar/ocultar formulario de categoría
                Button(
                    onClick = {
                        mostrarFormularioCategoria = !mostrarFormularioCategoria
                        if (mostrarFormularioCategoria) {
                            // Limpiar el formulario al abrir
                            nombreCategoriaActual = ""
                            nuevaOpcion = ""
                            opcionesActuales = mutableListOf()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (mostrarFormularioCategoria) "Ocultar Formulario" else "Agregar Categoría")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario para agregar nueva categoría
                if (mostrarFormularioCategoria) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Nombre de la categoría
                            OutlinedTextField(
                                value = nombreCategoriaActual,
                                onValueChange = { nombreCategoriaActual = it },
                                label = { Text("Nombre de la categoría") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Campo para agregar opciones
                            OutlinedTextField(
                                value = nuevaOpcion,
                                onValueChange = { nuevaOpcion = it },
                                label = { Text("Nueva opción") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botón confirmar opción
                            Button(
                                onClick = {
                                    if (nuevaOpcion.isNotBlank()) {
                                        val nuevasOpciones = opcionesActuales.toMutableList()
                                        nuevasOpciones.add(nuevaOpcion.trim())
                                        opcionesActuales = nuevasOpciones
                                        nuevaOpcion = ""
                                    }
                                },
                                enabled = nuevaOpcion.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Agregar Opción")
                            }

                            // Lista de opciones agregadas
                            if (opcionesActuales.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Opciones agregadas:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 150.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(opcionesActuales.indices.toList()) { index ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "• ${opcionesActuales[index]}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    val nuevasOpciones = opcionesActuales.toMutableList()
                                                    nuevasOpciones.removeAt(index)
                                                    opcionesActuales = nuevasOpciones
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar opción",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón confirmar categoría
                            Button(
                                onClick = {
                                    if (nombreCategoriaActual.isNotBlank()) {
                                        // Crear la nueva categoría
                                        val nuevaCategoria = CategoriaServicio(
                                            nombre = nombreCategoriaActual.trim(),
                                            opciones = opcionesActuales.toList()
                                        )

                                        // Agregar a la lista de categorías confirmadas
                                        val nuevasCategoriasConfirmadas = categoriasConfirmadas.toMutableList()
                                        nuevasCategoriasConfirmadas.add(nuevaCategoria)
                                        categoriasConfirmadas = nuevasCategoriasConfirmadas

                                        // Limpiar el formulario
                                        nombreCategoriaActual = ""
                                        nuevaOpcion = ""
                                        opcionesActuales = mutableListOf()

                                        // Ocultar el formulario
                                        mostrarFormularioCategoria = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = nombreCategoriaActual.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Confirmar")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirmar Categoría")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Mostrar categorías confirmadas
                if (categoriasConfirmadas.isNotEmpty()) {
                    Text(
                        "Categorías confirmadas:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoriasConfirmadas.indices.toList()) { index ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            categoriasConfirmadas[index].nombre,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                val nuevasCategoriasConfirmadas = categoriasConfirmadas.toMutableList()
                                                nuevasCategoriasConfirmadas.removeAt(index)
                                                categoriasConfirmadas = nuevasCategoriasConfirmadas
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar categoría",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    if (categoriasConfirmadas[index].opciones.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Opciones: ${categoriasConfirmadas[index].opciones.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay categorías agregadas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val precio = precioTexto.toDoubleOrNull()
                if (nombre.isNotBlank() && precio != null) {
                    // Debug: mostrar qué categorías se van a guardar
                    println("Categorías a guardar: ${categoriasConfirmadas.map { "${it.nombre}: ${it.opciones}" }}")

                    viewModel.agregarServicio(
                        nombre.trim(),
                        descripcion.trim(),
                        categoriasConfirmadas.toList(),
                        precio,
                        onSuccess = {
                            mensaje = "Servicio agregado correctamente"
                            nombre = ""
                            descripcion = ""
                            precioTexto = ""
                            categoriasConfirmadas = mutableListOf()
                            mostrarFormularioCategoria = false
                        },
                        onFailure = {
                            mensaje = "Error: ${it.message}"
                        }
                    )
                } else {
                    mensaje = "Faltan datos válidos"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && precioTexto.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Guardar Servicio", style = MaterialTheme.typography.titleMedium)
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (mensaje.contains("Error"))
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(12.dp),
                    color = if (mensaje.contains("Error"))
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}