package com.example.gestoreventos.model

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
    val anticipo: Double = 0.0
)

data class ServicioSeleccionado(
    val idServicio: String = "",
    val cantidad: Int = 1,  // NUEVO CAMPO - cantidad de piezas/unidades
    val categoriasSeleccionadas: List<CategoriaSeleccionada> = emptyList()
)

data class CategoriaSeleccionada(
    val nombreCategoria: String = "",
    val opcionesSeleccionadas: List<String> = emptyList()
)