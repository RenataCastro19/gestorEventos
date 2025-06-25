package com.example.gestoreventos.model

data class Servicio(
    val id: String = "",
    val nombre: String = "",               // Ej: "Esquites", "Micheladas", "Aguas", "Snacks"
    val descripcion: String = "",
    val categoriasDetalle: List<String> = emptyList(), // Ej: ["toppings", "tipoGrano"], ["licores", "vasos"]
    val precioPorPersona: Double = 0.0
)
