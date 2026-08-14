package com.example.gestoreventos.model

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categorias: List<CategoriaServicio> = emptyList(),
    val estado: String = "activo",
    val checklistTemplate: List<ChecklistCategoria> = emptyList()
)

data class CategoriaServicio(
    val nombre: String = "",
    val opciones: List<String> = emptyList()
)

data class ChecklistCategoria(
    val nombre: String = "", // Ej: "Caja", "Hielera"
    val items: List<String> = emptyList() // Ej: ["extensión", "Cucharón esquites c/hoyos"]
)