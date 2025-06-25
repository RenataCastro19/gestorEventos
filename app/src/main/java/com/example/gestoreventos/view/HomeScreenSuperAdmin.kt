package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenSuperAdmin(
    onMobiliarioClick: () -> Unit = {},
    onEmpleadosClick: () -> Unit = {},
    onEventosClick: () -> Unit = {},
    onServiciosClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Button(onClick = onMobiliarioClick, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text("Mobiliario")
            }
            Button(onClick = onEmpleadosClick, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text("Empleados")
            }
            Button(onClick = onEventosClick, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text("Eventos")
            }
            Button(onClick = onServiciosClick, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text("Servicios")
            }
        }
    }
}
