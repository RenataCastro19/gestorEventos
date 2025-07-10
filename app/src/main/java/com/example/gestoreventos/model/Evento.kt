package com.example.gestoreventos.model

data class Evento(
    val id: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val numeroPersonas: Int = 0,
    val idCliente: String = "",
    val direccionEvento: String = "",  // Dirección del evento específica
    val listaIdsEmpleados: List<String> = emptyList(),
    val idMobiliario: String = "",
    val idServicio: String = "",
    val detalleServicio: String = "",
    val serviciosSeleccionados: List<ServicioSeleccionado> = emptyList() // <-- Nuevo campo
)

data class ServicioSeleccionado(
    val idServicio: String = "",
    val categoriasSeleccionadas: List<CategoriaSeleccionada> = emptyList()
)

data class CategoriaSeleccionada(
    val nombreCategoria: String = "",
    val opcionesSeleccionadas: List<String> = emptyList()
)

