package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Evento
import com.google.firebase.firestore.FirebaseFirestore

class EventoRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarEvento(
        evento: Evento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("eventos")
            .document(evento.id)
            .set(evento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun obtenerEventos(onResult: (List<Evento>) -> Unit) {
        db.collection("eventos").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Evento::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
