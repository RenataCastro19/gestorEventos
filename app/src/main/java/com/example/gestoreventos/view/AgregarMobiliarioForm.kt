@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.SuccessGreen
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
    var esError by remember { mutableStateOf(false) }
    var categorias by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }
    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaMobiliario?>(null) }

    LaunchedEffect(Unit) {
        categoriaViewModel.obtenerCategorias { lista ->
            categorias = lista
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Agregar Mobiliario",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
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

        Button(
            onClick = {
                if (categoriaSeleccionada != null && color.isNotBlank()) {
                    viewModel.agregarMobiliario(
                        idCategoria = categoriaSeleccionada!!.id,
                        color = color,
                        onSuccess = {
                            mensaje = "Mobiliario agregado correctamente"
                            esError = false
                            categoriaSeleccionada = null
                            color = ""
                        },
                        onFailure = {
                            mensaje = "Error: ${it.message}"
                            esError = true
                        }
                    )
                } else {
                    mensaje = "Selecciona categoría y escribe color"
                    esError = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                contentColor = BrandBlack
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = if (esError) ErrorRed else SuccessGreen
            )
        }
    }
}