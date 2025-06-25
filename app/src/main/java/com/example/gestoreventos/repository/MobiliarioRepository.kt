package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Mobiliario
import com.google.firebase.firestore.FirebaseFirestore

class MobiliarioRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarMobiliario(
        mobiliario: Mobiliario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("mobiliarios")  // plural
            .document(mobiliario.id)
            .set(mobiliario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        db.collection("mobiliarios")  // plural
            .document(id).get()
            .addOnSuccessListener { doc -> onResult(!doc.exists()) }
            .addOnFailureListener { onResult(false) }
    }

    fun obtenerMobiliarios(onResult: (List<Mobiliario>) -> Unit) {
        db.collection("mobiliarios")  // plural
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Mobiliario::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
