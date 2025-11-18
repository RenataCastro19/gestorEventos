package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Servicio
import com.google.firebase.firestore.FirebaseFirestore

class ServicioRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agregarServicio(
        servicio: Servicio,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("servicios")
            .document(servicio.id)
            .set(servicio)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun verificarIdDisponible(id: String, onResult: (Boolean) -> Unit) {
        db.collection("servicios").document(id).get()
            .addOnSuccessListener { doc -> onResult(!doc.exists()) }
            .addOnFailureListener { onResult(false) }
    }

    fun obtenerServicios(onResult: (List<Servicio>) -> Unit) {
        db.collection("servicios").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Servicio::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun obtenerServicioPorId(servicioId: String, onResult: (Servicio?) -> Unit) {
        db.collection("servicios")
            .document(servicioId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val servicio = document.toObject(Servicio::class.java)
                    onResult(servicio)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                println("Error al obtener servicio: ${e.message}")
                onResult(null)
            }
    }

    fun actualizarServicio(
        servicio: Servicio,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("servicios")
            .document(servicio.id)
            .set(servicio)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}