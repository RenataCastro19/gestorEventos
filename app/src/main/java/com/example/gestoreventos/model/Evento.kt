package com.example.gestoreventos.model

// MODELO ACTUALIZADO DE EVENTO - Actualiza tu Evento.kt
data class Evento(
    val id: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val numeroPersonas: Int = 0,
    val idCliente: String = "",
    val direccionEvento: String = "",
    val listaIdsEmpleados: List<String> = emptyList(),
    val idMobiliario: String = "",
    val idServicio: String = "",
    val detalleServicio: String = "",
    val serviciosSeleccionados: List<ServicioSeleccionado> = emptyList(),
    val precioTotal: Double = 0.0,
    val anticipo: Double = 0.0,
    val checklists: Map<String, ChecklistEvento> = emptyMap() // NUEVO: Checklists por servicio
)

data class ServicioSeleccionado(
    val idServicio: String = "",
    val cantidad: Int = 1,
    val categoriasSeleccionadas: List<CategoriaSeleccionada> = emptyList()
)

data class CategoriaSeleccionada(
    val nombreCategoria: String = "",
    val opcionesSeleccionadas: List<String> = emptyList()
)

// NUEVO: Checklist de un evento específico
data class ChecklistEvento(
    val idServicio: String = "",
    val nombreServicio: String = "",
    val categorias: List<ChecklistCategoriaEvento> = emptyList()
)

data class ChecklistCategoriaEvento(
    val nombre: String = "",
    val items: List<ChecklistItem> = emptyList()
)

data class ChecklistItem(
    val nombre: String = "",
    val completado: Boolean = false
)