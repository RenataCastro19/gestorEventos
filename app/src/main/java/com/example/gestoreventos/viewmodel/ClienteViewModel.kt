package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.repository.ClienteRepository

class ClienteViewModel : ViewModel() {
    private val repository = ClienteRepository()

    fun agregarCliente(
        nombre: String,
        telefono: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val id = generateId() // Puedes usar UUID o lógica propia
        val cliente = Cliente(id, nombre, telefono)
        repository.agregarCliente(cliente, onSuccess, onFailure)
    }

    fun obtenerClientes(onResult: (List<Cliente>) -> Unit) {
        repository.obtenerClientes(onResult)
    }

    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
