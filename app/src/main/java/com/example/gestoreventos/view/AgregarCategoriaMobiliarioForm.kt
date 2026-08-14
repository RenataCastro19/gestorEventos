package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.SuccessGreen
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel

@Composable
fun AgregarCategoriaMobiliarioForm(
    viewModel: CategoriaMobiliarioViewModel = CategoriaMobiliarioViewModel(),
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Agregar Categoría de Mobiliario",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank()) {
                    viewModel.agregarCategoria(
                        nombre,
                        onSuccess = {
                            mensaje = "Categoría agregada correctamente"
                            esError = false
                            nombre = ""
                        },
                        onFailure = {
                            mensaje = "Error: ${it.message}"
                            esError = true
                        }
                    )
                } else {
                    mensaje = "El nombre es obligatorio"
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