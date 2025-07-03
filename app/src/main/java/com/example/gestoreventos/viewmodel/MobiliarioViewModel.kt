package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Mobiliario
import com.example.gestoreventos.model.CategoriaMobiliario
import com.example.gestoreventos.repository.MobiliarioRepository
import com.example.gestoreventos.repository.CategoriaMobiliarioRepository

class MobiliarioViewModel : ViewModel() {
    private val repository = MobiliarioRepository()
    private val categoriaRepository = CategoriaMobiliarioRepository()

    fun agregarMobiliario(
        idCategoria: String,
        color: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        generarIdUnico { idSeguro ->
            val mobiliario = Mobiliario(id = idSeguro, idCategoria = idCategoria, color = color)
            repository.agregarMobiliario(mobiliario, onSuccess, onFailure)
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
    fun obtenerMobiliario(onResult: (List<Mobiliario>) -> Unit) {
        repository.obtenerMobiliarios(onResult)
    }

    fun obtenerCategoriasMobiliario(onResult: (List<CategoriaMobiliario>) -> Unit) {
        categoriaRepository.obtenerCategorias(onResult)
    }

    fun inhabilitarMobiliario(mobiliario: Mobiliario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val inhabilitado = mobiliario.copy(estado = "inhabilitado")
        repository.actualizarMobiliario(
            mobiliario = inhabilitado,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun habilitarMobiliario(mobiliario: Mobiliario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val habilitado = mobiliario.copy(estado = "activo")
        repository.actualizarMobiliario(
            mobiliario = habilitado,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

}
