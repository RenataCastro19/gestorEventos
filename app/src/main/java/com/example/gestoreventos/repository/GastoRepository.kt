package com.example.gestoreventos.repository

import com.example.gestoreventos.model.Gasto
import com.google.firebase.firestore.FirebaseFirestore

class GastoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("gastos")

    fun agregarGasto(gasto: Gasto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // A diferencia de Servicio/Evento (que usan un ID random de 4 dígitos con verificación
        // de colisión), aquí usamos el ID automático de Firestore. Con eventos/servicios tiene
        // sentido un ID cortito porque hay pocos y a veces los escribes a mano; los gastos se
        // van a acumular por decenas cada mes, así que un ID único generado por Firestore es
        // más seguro y no necesita el paso extra de "verificar si ya existe".
        val docRef = coleccion.document()
        val gastoConId = gasto.copy(id = docRef.id)
        docRef.set(gastoConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun obtenerGastos(onResult: (List<Gasto>) -> Unit) {
        coleccion.get()
            .addOnSuccessListener { snapshot ->
                val gastos = snapshot.documents.mapNotNull { it.toObject(Gasto::class.java) }
                onResult(gastos)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun eliminarGasto(gastoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        coleccion.document(gastoId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}