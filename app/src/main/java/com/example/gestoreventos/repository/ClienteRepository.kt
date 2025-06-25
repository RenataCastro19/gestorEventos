package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Cliente
import com.google.firebase.firestore.FirebaseFirestore

class ClienteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarCliente(cliente: Cliente, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("clientes")
            .document(cliente.id)
            .set(cliente)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun obtenerClientes(onResult: (List<Cliente>) -> Unit) {
        db.collection("clientes").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Cliente::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
