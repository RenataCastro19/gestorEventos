package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.repository.ClienteRepository

class ClienteViewModel : ViewModel() {
    private val repository = ClienteRepository()

    fun agregarCliente(
        nombre: String,
        telefono: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        generarIdUnico { idSeguro ->
            val cliente = Cliente(id = idSeguro, nombre = nombre, telefono = telefono)
            repository.agregarCliente(cliente, { onSuccess(idSeguro) }, onFailure)
        }
    }

    fun obtenerClientes(onResult: (List<Cliente>) -> Unit) {
        repository.obtenerClientes(onResult)
    }

    private fun generarIdUnico(onIdGenerado: (String) -> Unit) {
        val intentosMaximos = 20
        var intentos = 0

        fun intentar() {
            val nuevoId = (1000..9999).random().toString()
            repository.verificarIdDisponible(nuevoId) { disponible ->
                if (disponible) {
                    onIdGenerado(nuevoId)
                } else {
                    intentos++
                    if (intentos < intentosMaximos) {
                        intentar()
                    } else {
                        onIdGenerado((10000..99999).random().toString())
                    }
                }
            }
        }

        intentar()
    }
}
