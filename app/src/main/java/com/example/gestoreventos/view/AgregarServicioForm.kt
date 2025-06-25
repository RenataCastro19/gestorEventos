package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.viewmodel.ServicioViewModel

@Composable
fun AgregarServicioForm(
    modifier: Modifier = Modifier,
    viewModel: ServicioViewModel = ServicioViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var categoriaInput by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf(mutableListOf<String>()) }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Agregar Servicio", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del servicio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = precioTexto,
            onValueChange = { precioTexto = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Precio por persona") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Categorías de detalle (Ej: toppings, tipoGrano, licores)", style = MaterialTheme.typography.bodyMedium)

        Row {
            OutlinedTextField(
                value = categoriaInput,
                onValueChange = { categoriaInput = it },
                label = { Text("Nueva categoría") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (categoriaInput.isNotBlank()) {
                    categorias.add(categoriaInput.trim())
                    categoriaInput = ""
                }
            }) {
                Text("Agregar")
            }
        }

        if (categorias.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Categorías añadidas: ${categorias.joinToString()}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val precio = precioTexto.toDoubleOrNull()
            if (nombre.isNotBlank() && precio != null) {
                viewModel.agregarServicio(
                    nombre.trim(),
                    descripcion.trim(),
                    categorias.toList(),
                    precio,
                    onSuccess = {
                        mensaje = "Servicio agregado correctamente"
                        nombre = ""
                        descripcion = ""
                        precioTexto = ""
                        categorias.clear()
                    },
                    onFailure = {
                        mensaje = "Error: ${it.message}"
                    }
                )
            } else {
                mensaje = "Faltan datos válidos"
            }
        }) {
            Text("Guardar Servicio")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensaje)
        }
    }
}
