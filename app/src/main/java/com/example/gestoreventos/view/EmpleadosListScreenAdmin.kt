package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.viewmodel.SuperAdminViewModel
import com.example.gestoreventos.viewmodel.EventoViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.view.EventosEmpleadoDialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun EmpleadosListScreenAdmin(
    viewModel: SuperAdminViewModel = viewModel()
) {
    val empleados by viewModel.empleados.collectAsState()
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val empleadosFiltrados = empleados.filter { it.rol != "super_admin" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Título de la sección
        Text(
            text = "Gestión de Empleados",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Título del listado
        Text(
            text = "Personal Registrado",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Listado elegante
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(empleadosFiltrados) { empleado ->
                ElegantEmpleadoItemAdmin(
                    empleado = empleado,
                    onInhabilitar = { usuario ->
                        viewModel.inhabilitarUsuario(
                            usuario = usuario,
                            onSuccess = {},
                            onFailure = { exception -> println("Error al inhabilitar usuario: ${exception.message}") }
                        )
                    },
                    onHabilitar = { usuario ->
                        viewModel.habilitarUsuario(
                            usuario = usuario,
                            onSuccess = {},
                            onFailure = { exception -> println("Error al habilitar usuario: ${exception.message}") }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ElegantEmpleadoItemAdmin(
    empleado: Usuario,
    onInhabilitar: (Usuario) -> Unit,
    onHabilitar: (Usuario) -> Unit
) {
    var mostrarEventos by remember { mutableStateOf(false) }
    var eventosEmpleado by remember { mutableStateOf<List<Evento>>(emptyList()) }
    val eventoViewModel: EventoViewModel = viewModel()

    // Cargar eventos del empleado cuando se abre el diálogo
    LaunchedEffect(mostrarEventos) {
        if (mostrarEventos) {
            eventoViewModel.obtenerEventos { todosLosEventos ->
                eventosEmpleado = todosLosEventos.filter { evento ->
                    evento.listaIdsEmpleados.contains(empleado.id)
                }.sortedBy { evento ->
                    // Ordenar por fecha (más pronto primero)
                    evento.fecha
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { mostrarEventos = true }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (empleado.estado == "inhabilitado") Color.Gray.copy(alpha = 0.2f) else BrandGold.copy(alpha = 0.2f)
            )
            .border(
                width = 1.dp,
                color = if (empleado.estado == "inhabilitado") Color.Gray.copy(alpha = 0.3f) else BrandGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (empleado.estado == "inhabilitado") Color.Gray.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${empleado.id}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (empleado.estado == "inhabilitado") Color.Gray else BrandGold
                    )
                )

                // Indicador de rol
                Card(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (empleado.estado == "inhabilitado") Color.Gray else BrandGold,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (empleado.estado == "inhabilitado") Color.Gray.copy(alpha = 0.1f) else BrandGold.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = empleado.rol.uppercase(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (empleado.estado == "inhabilitado") Color.Gray else BrandGold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = "${empleado.nombre} ${empleado.apellidoPaterno} ${empleado.apellidoMaterno}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (empleado.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Teléfono",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = empleado.telefono,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (empleado.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )

                if (empleado.estado == "inhabilitado") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ESTADO: INHABILITADO",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    )
                }
            }
        }
    }

    // Diálogo de eventos del empleado
    if (mostrarEventos) {
        EventosEmpleadoDialog(
            empleado = empleado,
            eventos = eventosEmpleado,
            onDismiss = { mostrarEventos = false },
            onInhabilitar = { onInhabilitar(empleado); mostrarEventos = false },
            onHabilitar = { onHabilitar(empleado); mostrarEventos = false }
        )
    }
}

