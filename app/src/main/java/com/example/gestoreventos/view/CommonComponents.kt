package com.example.gestoreventos.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.BrandBlack
import com.example.gestoreventos.ui.theme.CardBorder
import com.example.gestoreventos.ui.theme.SuccessGreen
import com.example.gestoreventos.ui.theme.SuccessGreenBg
import com.example.gestoreventos.ui.theme.ErrorRed
import com.example.gestoreventos.ui.theme.ErrorRedBg

@Composable
fun ElegantButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrandGold
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun EventosEmpleadoDialog(
    empleado: Usuario,
    eventos: List<Evento>,
    onDismiss: () -> Unit,
    onInhabilitar: () -> Unit,
    onHabilitar: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier.padding(24.dp).widthIn(min = 300.dp, max = 500.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Eventos de ${empleado.nombre}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandGold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        if (empleado.estado == "inhabilitado") {
                            Button(
                                onClick = onHabilitar,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreenBg,
                                    contentColor = SuccessGreen
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Habilitar", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Button(
                                onClick = onInhabilitar,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ErrorRedBg,
                                    contentColor = ErrorRed
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Inhabilitar", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (eventos.isEmpty()) {
                        Text(
                            text = "No hay eventos asignados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(eventos.size) { index ->
                                val evento = eventos[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = CardBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "Evento: ${evento.fecha}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = BrandGold
                                            )
                                        )
                                        Text(
                                            text = "Hora: ${evento.horaInicio} - ${evento.horaFin}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Personas: ${evento.numeroPersonas}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Dirección: ${evento.direccionEvento}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}