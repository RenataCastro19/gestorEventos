package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.model.CategoriaServicio
import com.example.gestoreventos.repository.ServicioRepository

class ServicioViewModel : ViewModel() {
    private val repository = ServicioRepository()

    fun agregarServicio(
        nombre: String,
        descripcion: String,
        categorias: List<CategoriaServicio>,
        precioPorPersona: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        generarIdUnico { idSeguro ->
            val servicio = Servicio(
                id = idSeguro,
                nombre = nombre,
                descripcion = descripcion,
                categorias = categorias,
                precioPorPersona = precioPorPersona
            )
            repository.agregarServicio(servicio, onSuccess, onFailure)
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
                    if (intentos < intentosMaximos) intentar()
                    else onIdGenerado((10000..99999).random().toString())
                }
            }
        }

        intentar()
    }

    fun obtenerServicios(onResult: (List<Servicio>) -> Unit) {
        repository.obtenerServicios(onResult)
    }

    fun obtenerServicioPorId(
        servicioId: String,
        onResult: (Servicio?) -> Unit
    ) {
        repository.obtenerServicioPorId(servicioId, onResult)
    }

    fun inhabilitarServicio(servicio: Servicio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val inhabilitado = servicio.copy(estado = "inhabilitado")
        repository.actualizarServicio(
            servicio = inhabilitado,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun habilitarServicio(servicio: Servicio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val habilitado = servicio.copy(estado = "activo")
        repository.actualizarServicio(
            servicio = habilitado,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun actualizarServicio(
        servicio: Servicio,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.actualizarServicio(
            servicio = servicio,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}