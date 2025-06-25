package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.viewmodel.MobiliarioViewModel
import com.example.gestoreventos.viewmodel.CategoriaMobiliarioViewModel

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAgregarCategoriaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Categoría")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAgregarMobiliarioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Mobiliario")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Listado de Mobiliario", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(mobiliarioList) { mobiliario ->
                val categoriaNombre = categoriaMap[mobiliario.idCategoria]?.nombre ?: "Sin categoría"
                MobiliarioItem(mobiliario, categoriaNombre)
            }
        }
    }
}

@Composable
fun MobiliarioItem(mobiliario: Mobiliario, categoriaNombre: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text("ID: ${mobiliario.id}")
        Text("Categoría: $categoriaNombre")
        Text("Color: ${mobiliario.color}")
    }
}