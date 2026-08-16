package com.example.gestoreventos.model

data class Gasto(
    val id: String = "",
    val producto: String = "",
    val monto: Double = 0.0,
    val fecha: String = "", // formato dd/MM/yyyy, igual que Evento
    val mes: String = "", // formato yyyy-MM, para poder filtrar por mes rápido más adelante
    val idUsuario: String = "",
    val nombreUsuario: String = "" // guardado directo aquí para no tener que ir a buscarlo cada vez que se muestra la lista
)