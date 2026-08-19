package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Evento
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

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
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
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

    fun actualizarEvento(
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

    fun obtenerEventoPorId(eventoId: String, onResult: (Evento?) -> Unit) {
        db.collection("eventos").document(eventoId).get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(Evento::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Obtiene todos los eventos como Task para usar con corutinas
     */
    fun obtenerTodosLosEventos(): Task<QuerySnapshot> {
        return db.collection("eventos").get()
    }
}