@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel
import com.example.gestoreventos.viewmodel.MobiliarioViewModel

@Composable
fun AgregarMobiliarioForm(
    viewModel: MobiliarioViewModel = MobiliarioViewModel(),
    categoriaViewModel: CategoriaMobiliarioViewModel = CategoriaMobiliarioViewModel(),
    modifier: Modifier = Modifier
) {
    var color by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaMobiliario?>(null) }

    LaunchedEffect(Unit) {
        categoriaViewModel.obtenerCategorias { lista ->
            categorias = lista
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Agregar Mobiliario", style = MaterialTheme.typography.titleLarge)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = categoriaSeleccionada?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nombre) },
                        onClick = {
                            categoriaSeleccionada = categoria
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (categoriaSeleccionada != null && color.isNotBlank()) {
                viewModel.agregarMobiliario(
                    idCategoria = categoriaSeleccionada!!.id,
                    color = color,
                    onSuccess = {
                        mensaje = "Mobiliario agregado correctamente"
                        categoriaSeleccionada = null
                        color = ""
                    },
                    onFailure = {
                        mensaje = "Error: ${it.message}"
                    }
                )
            } else {
                mensaje = "Selecciona categoría y escribe color"
            }
        }) {
            Text("Guardar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensaje)
        }
    }
}
