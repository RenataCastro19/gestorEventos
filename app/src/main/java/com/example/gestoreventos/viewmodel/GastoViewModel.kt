package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Gasto
import com.example.gestoreventos.repository.GastoRepository

class GastoViewModel : ViewModel() {
    private val repository = GastoRepository()

    fun agregarGasto(
        producto: String,
        monto: Double,
        fecha: String,
        mes: String,
        idUsuario: String,
        nombreUsuario: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val gasto = Gasto(
            producto = producto,
            monto = monto,
            fecha = fecha,
            mes = mes,
            idUsuario = idUsuario,
            nombreUsuario = nombreUsuario
        )
        repository.agregarGasto(gasto, onSuccess, onFailure)
    }

    fun obtenerGastos(onResult: (List<Gasto>) -> Unit) {
        repository.obtenerGastos(onResult)
    }

    fun eliminarGasto(gastoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repository.eliminarGasto(gastoId, onSuccess, onFailure)
    }
}