package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Pago
import com.example.gestoreventos.repository.PagoRepository

class PagoViewModel : ViewModel() {
    private val repository = PagoRepository()

    fun agregarPago(
        idEvento: String,
        monto: Double,
        tipo: String,
        fecha: String,
        mes: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val pago = Pago(
            idEvento = idEvento,
            monto = monto,
            tipo = tipo,
            fecha = fecha,
            mes = mes
        )
        repository.agregarPago(pago, onSuccess, onFailure)
    }

    fun obtenerPagos(onResult: (List<Pago>) -> Unit) {
        repository.obtenerPagos(onResult)
    }

    fun obtenerPagosDeEvento(idEvento: String, onResult: (List<Pago>) -> Unit) {
        repository.obtenerPagosDeEvento(idEvento, onResult)
    }
}