package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Cliente
import com.google.firebase.firestore.FirebaseFirestore

class ClienteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarCliente(cliente: Cliente, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        println("DEBUG: Guardando cliente en Firebase - ID: '${cliente.id}', Nombre: '${cliente.nombre}'")
        db.collection("clientes")
            .document(cliente.id)
            .set(cliente)
            .addOnSuccessListener {
                println("DEBUG: Cliente guardado exitosamente en Firebase")
                onSuccess()
            }
            .addOnFailureListener { e ->
                println("DEBUG: Error al guardar cliente: ${e.message}")
                onFailure(e)
            }
    }

    fun obtenerClientes(onResult: (List<Cliente>) -> Unit) {
        println("DEBUG: Cargando clientes desde Firebase...")
        db.collection("clientes").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Cliente::class.java) }
                println("DEBUG: Clientes cargados desde Firebase: ${lista.size}")
                lista.forEach { cliente ->
                    println("DEBUG: Cliente cargado - ID: '${cliente.id}', Nombre: '${cliente.nombre}'")
                }
                onResult(lista)
            }
            .addOnFailureListener { error ->
                println("DEBUG: Error al cargar clientes: ${error.message}")
                onResult(emptyList())
            }
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        db.collection("clientes").document(id).get()
            .addOnSuccessListener { doc -> onResult(!doc.exists()) }
            .addOnFailureListener { onResult(false) }
    }
}
