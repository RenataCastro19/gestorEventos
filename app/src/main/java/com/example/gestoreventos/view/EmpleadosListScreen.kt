package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.viewmodel.SuperAdminViewModel

@Composable
fun EmpleadosListScreen(
    onAgregarEmpleadoClick: () -> Unit = {},
    viewModel: SuperAdminViewModel = SuperAdminViewModel()
) {
    val empleados by viewModel.empleados.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAgregarEmpleadoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Empleado")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Listado de Empleados", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(empleados) { empleado ->
                EmpleadoItem(empleado)
            }
        }
    }
}

@Composable
fun EmpleadoItem(empleado: Usuario) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text("ID: ${empleado.id}")
        Text("Nombre: ${empleado.nombre}")
        Text("Rol: ${empleado.rol}")
    }
}