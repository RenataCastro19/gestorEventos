package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Pago
import com.google.firebase.firestore.FirebaseFirestore

class PagoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("pagos")

    fun agregarPago(pago: Pago, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = coleccion.document()
        val pagoConId = pago.copy(id = docRef.id)
        docRef.set(pagoConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun obtenerPagos(onResult: (List<Pago>) -> Unit) {
        coleccion.get()
            .addOnSuccessListener { snapshot ->
                val pagos = snapshot.documents.mapNotNull { it.toObject(Pago::class.java) }
                onResult(pagos)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Todos los pagos de un evento en particular, para calcular su saldo pendiente
    fun obtenerPagosDeEvento(idEvento: String, onResult: (List<Pago>) -> Unit) {
        coleccion.whereEqualTo("idEvento", idEvento).get()
            .addOnSuccessListener { snapshot ->
                val pagos = snapshot.documents.mapNotNull { it.toObject(Pago::class.java) }
                onResult(pagos)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}