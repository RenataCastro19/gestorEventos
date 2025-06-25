package com.example.gestoreventos.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val telefono: String = "",
    val contrasena: String = "", // Cambié "contraseña" por "contrasena" para consistencia
    val rol: String = "empleado" // "super_admin", "admin", "empleado"
)