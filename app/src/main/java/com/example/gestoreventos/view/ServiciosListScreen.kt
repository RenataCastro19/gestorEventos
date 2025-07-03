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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.viewmodel.ServicioViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import androidx.compose.ui.graphics.Color

@Composable
fun ServiciosListScreen(
    onAgregarServicioClick: () -> Unit = {},
    viewModel: ServicioViewModel = ServicioViewModel()
) {
    var servicios by remember { mutableStateOf(listOf<Servicio>()) }

    LaunchedEffect(Unit) {
        viewModel.obtenerServicios { lista ->
            servicios = lista
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Título de la sección
        Text(
            text = "Gestión de Servicios",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botón de acción
        ServiciosButton(
            text = "Agregar Servicio",
            onClick = onAgregarServicioClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título del listado
        Text(
            text = "Catálogo de Servicios",
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
            items(servicios) { servicio ->
                ElegantServicioItem(
                    servicio = servicio,
                    viewModel = viewModel,
                    onRecargarLista = {
                        viewModel.obtenerServicios { lista ->
                            servicios = lista
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ElegantServicioItem(
    servicio: Servicio,
    viewModel: ServicioViewModel,
    onRecargarLista: () -> Unit
) {
    var mostrarDetalles by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { mostrarDetalles = true }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = BrandGold.copy(alpha = 0.2f)
            )
            .border(
                width = 1.dp,
                color = BrandGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (servicio.estado == "inhabilitado") Color.Gray.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
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
                Column {
                    Text(
                        text = "ID: ${servicio.id}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (servicio.estado == "inhabilitado") Color.Gray else BrandGold
                        )
                    )
                    if (servicio.estado == "inhabilitado") {
                        Text(
                            text = "ESTADO: INHABILITADO",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        )
                    }
                }
                if (servicio.estado == "inhabilitado") {
                    Button(
                        onClick = {
                            viewModel.habilitarServicio(servicio, onSuccess = { onRecargarLista() }, onFailure = {})
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // Verde
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Habilitar", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.inhabilitarServicio(servicio, onSuccess = { onRecargarLista() }, onFailure = {})
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Inhabilitar", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Información principal
            Column {
                Text(
                    text = "Nombre del Servicio",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (servicio.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = servicio.descripcion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (servicio.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
    if (mostrarDetalles) {
        DetalleServicioDialog(
            servicio = servicio,
            onDismiss = { mostrarDetalles = false }
        )
    }
}

@Composable
fun DetalleServicioDialog(
    servicio: Servicio,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(min = 300.dp, max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalle del Servicio",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BrandGold)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Servicio: ${servicio.nombre}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = servicio.descripcion,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun ServiciosButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = BrandGold.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = BrandGold,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrandGold
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ),
            maxLines = 2
        )
    }
}