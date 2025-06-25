package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.viewmodel.SuperAdminViewModel

@Composable
fun EventosListScreen(
    onAgregarEventoClick: () -> Unit = {},
    viewModel: SuperAdminViewModel = SuperAdminViewModel()
) {
    val eventos by viewModel.eventos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAgregarEventoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Evento")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Listado de Eventos", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(eventos) { evento ->
                EventoItem(evento)
            }
        }
    }
}

@Composable
fun EventoItem(evento: Evento) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text("ID: ${evento.id}")
        Text("Fecha: ${evento.fecha}")
        Text("Detalle: ${evento.detalleServicio}")
    }
}