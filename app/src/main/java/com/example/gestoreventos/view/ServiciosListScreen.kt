package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.viewmodel.SuperAdminViewModel

@Composable
fun ServiciosListScreen(
    onAgregarServicioClick: () -> Unit = {},
    viewModel: SuperAdminViewModel = SuperAdminViewModel()
) {
    val servicios by viewModel.servicios.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAgregarServicioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Servicio")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Listado de Servicios", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(servicios) { servicio ->
                ServicioItem(servicio)
            }
        }
    }
}

@Composable
fun ServicioItem(servicio: Servicio) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text("ID: ${servicio.id}")
        Text("Nombre: ${servicio.nombre}")
        Text("Descripción: ${servicio.descripcion}")
    }
}