package com.example.gestoreventos.repository

import com.example.gestoreventos.model.CategoriaMobiliario
import com.google.firebase.firestore.FirebaseFirestore

class CategoriaMobiliarioRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarCategoria(
        categoria: CategoriaMobiliario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("categorias_mobiliario")
            .document(categoria.id)
            .set(categoria)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        db.collection("categorias_mobiliario").document(id).get()
            .addOnSuccessListener { doc -> onResult(!doc.exists()) }
            .addOnFailureListener { onResult(false) }
    }
    fun obtenerCategorias(onResult: (List<CategoriaMobiliario>) -> Unit) {
        db.collection("categorias_mobiliario").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(CategoriaMobiliario::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}
