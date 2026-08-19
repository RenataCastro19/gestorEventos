package com.example.gestoreventos.model

data class Pago(
    val id: String = "",
    val idEvento: String = "",
    val monto: Double = 0.0,
    val tipo: String = "", // "anticipo", "liquidacion", "abono"
    val fecha: String = "", // formato dd/MM/yyyy
    val mes: String = "" // formato yyyy-MM, para el corte mensual
)