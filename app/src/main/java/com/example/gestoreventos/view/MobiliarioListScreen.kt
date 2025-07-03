package com.example.gestoreventos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel
import com.example.gestoreventos.ui.theme.BrandGold
import com.example.gestoreventos.ui.theme.GrayLight
import androidx.compose.ui.graphics.Color

@Composable
fun MobiliarioListScreen(
    onAgregarMobiliarioClick: () -> Unit = {},
    onAgregarCategoriaClick: () -> Unit = {},
    viewModel: MobiliarioViewModel = MobiliarioViewModel(),
    categoriaViewModel: CategoriaMobiliarioViewModel = CategoriaMobiliarioViewModel()
) {
    var mobiliarioList by remember { mutableStateOf(listOf<Mobiliario>()) }
    var categorias by remember { mutableStateOf(listOf<CategoriaMobiliario>()) }

    LaunchedEffect(Unit) {
        viewModel.obtenerMobiliario { lista ->
            mobiliarioList = lista
        }
        categoriaViewModel.obtenerCategorias { lista ->
            categorias = lista
        }
    }

    val categoriaMap = categorias.associateBy { it.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Título de la sección
        Text(
            text = "Gestión de Mobiliario",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandGold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MobiliarioButton(
                text = "Agregar Categoría",
                onClick = onAgregarCategoriaClick,
                modifier = Modifier.weight(1f)
            )

            MobiliarioButton(
                text = "Agregar Mobiliario",
                onClick = onAgregarMobiliarioClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título del listado
        Text(
            text = "Inventario de Mobiliario",
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
            items(mobiliarioList) { mobiliario ->
                val categoriaNombre = categoriaMap[mobiliario.idCategoria]?.nombre ?: "Sin categoría"
                ElegantMobiliarioItem(
                    mobiliario = mobiliario,
                    categoriaNombre = categoriaNombre,
                    viewModel = viewModel,
                    onRecargarLista = {
                        viewModel.obtenerMobiliario { lista ->
                            mobiliarioList = lista
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ElegantMobiliarioItem(
    mobiliario: Mobiliario,
    categoriaNombre: String,
    viewModel: MobiliarioViewModel,
    onRecargarLista: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
            containerColor = if (mobiliario.estado == "inhabilitado") Color.Gray.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
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
                        text = "ID: ${mobiliario.id}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (mobiliario.estado == "inhabilitado") Color.Gray else BrandGold
                        )
                    )
                    if (mobiliario.estado == "inhabilitado") {
                        Text(
                            text = "ESTADO: INHABILITADO",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        )
                    }
                }
                // BOTÓN HABILITAR/INHABILITAR
                if (mobiliario.estado == "inhabilitado") {
                    Button(
                        onClick = {
                            viewModel.habilitarMobiliario(mobiliario, onSuccess = { onRecargarLista() }, onFailure = {})
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
                            viewModel.inhabilitarMobiliario(mobiliario, onSuccess = { onRecargarLista() }, onFailure = {})
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Categoría",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = categoriaNombre,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (mobiliario.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = mobiliario.color,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (mobiliario.estado == "inhabilitado") Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MobiliarioButton(
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