package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.repository.CategoriaMobiliarioRepository

class CategoriaMobiliarioViewModel : ViewModel() {
    private val repository = CategoriaMobiliarioRepository()

    fun agregarCategoria(
        nombre: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        generarIdUnico { idSeguro ->
            val categoria = CategoriaMobiliario(id = idSeguro, nombre = nombre)
            repository.agregarCategoria(categoria, onSuccess, onFailure)
        }
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
    fun obtenerCategorias(onResult: (List<CategoriaMobiliario>) -> Unit) {
        repository.obtenerCategorias(onResult)
    }

}
