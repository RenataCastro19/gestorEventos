package com.example.gestoreventos.model

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categorias: List<CategoriaServicio> = emptyList(), // Nueva estructura con categorías y opciones
    val precioPorPersona: Double = 0.0,
    val estado: String = "activo" // "activo" o "inhabilitado"
)

data class CategoriaServicio(
    val nombre: String = "",
    val opciones: List<String> = emptyList()
)
