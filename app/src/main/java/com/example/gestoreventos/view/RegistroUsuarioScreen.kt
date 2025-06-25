package com.example.gestoreventos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestoreventos.viewmodel.UsuarioViewModel

@Composable
fun RegistroUsuarioScreen(usuarioViewModel: UsuarioViewModel = viewModel()) {
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("empleado") }

    var mensaje by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val opcionesRol = listOf("super_admin", "admin", "empleado")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Registrar Usuario", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidoPaterno,
            onValueChange = { apellidoPaterno = it },
            label = { Text("Apellido Paterno") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidoMaterno,
            onValueChange = { apellidoMaterno = it },
            label = { Text("Apellido Materno") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("${telefono.length}/10") }
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Selecciona el rol:")

        Box {
            OutlinedTextField(
                value = rol,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesRol.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            rol = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nombre.isBlank() || contrasena.isBlank()) {
                mensaje = "Completa todos los campos obligatorios"
                isSuccess = false
                return@Button
            }

            usuarioViewModel.agregarUsuarioAutoId(
                nombre = nombre,
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno,
                telefono = telefono,
                contrasena = contrasena,
                rol = rol,
                onSuccess = {
                    mensaje = "Usuario registrado correctamente"
                    isSuccess = true
                    // Limpiar campos
                    nombre = ""
                    apellidoPaterno = ""
                    apellidoMaterno = ""
                    telefono = ""
                    contrasena = ""
                    rol = "empleado"
                },
                onFailure = { exception ->
                    mensaje = "Error: ${exception.message}"
                    isSuccess = false
                }
            )
        }) {
            Text("Registrar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = if (isSuccess) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}