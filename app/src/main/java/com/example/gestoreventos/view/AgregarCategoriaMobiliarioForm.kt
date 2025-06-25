package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel

@Composable
fun AgregarCategoriaMobiliarioForm(
    viewModel: CategoriaMobiliarioViewModel = CategoriaMobiliarioViewModel(),
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Agregar Categoría de Mobiliario", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nombre.isNotBlank()) {
                viewModel.agregarCategoria(
                    nombre,
                    onSuccess = {
                        mensaje = "Categoría agregada correctamente"
                        nombre = ""
                    },
                    onFailure = {
                        mensaje = "Error: ${it.message}"
                    }
                )
            } else {
                mensaje = "El nombre es obligatorio"
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
