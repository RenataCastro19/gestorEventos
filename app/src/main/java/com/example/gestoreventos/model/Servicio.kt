package com.example.gestoreventos.model

// MODELO ACTUALIZADO DE SERVICIO - Reemplaza tu Servicio.kt actual
data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categorias: List<CategoriaServicio> = emptyList(),
    val precioPorPersona: Double = 0.0,
    val estado: String = "activo",
    val checklistTemplate: List<ChecklistCategoria> = emptyList() // NUEVO: Template del checklist
)

data class CategoriaServicio(
    val nombre: String = "",
    val opciones: List<String> = emptyList()
)

// NUEVO: Estructura del checklist
data class ChecklistCategoria(
    val nombre: String = "", // Ej: "Caja", "Hielera"
    val items: List<String> = emptyList() // Ej: ["extensión", "Cucharón esquites c/hoyos"]
)
