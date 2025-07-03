package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.repository.EventoRepository
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventoViewModel : ViewModel() {
    private val repository = EventoRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    init {
        cargarEventos()
    }

    fun cargarEventos() {
        repository.obtenerEventos { lista ->
            _eventos.value = lista
        }
    }

    fun agregarEvento(
        evento: Evento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.agregarEvento(evento, {
            cargarEventos()
            onSuccess()
        }, onFailure)
    }

    fun obtenerEventos(onResult: (List<Evento>) -> Unit) {
        repository.obtenerEventos(onResult)
    }

    fun agregarEventoAutoId(
        fecha: String,
        horaInicio: String,
        horaFin: String,
        numeroPersonas: Int,
        idCliente: String,
        direccionEvento: String,
        listaIdsEmpleados: List<String>,
        idMobiliario: String,
        idServicio: String,
        detalleServicio: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val id = (10000..99999).random().toString()
        val evento = Evento(
            id = id,
            fecha = fecha,
            horaInicio = horaInicio,
            horaFin = horaFin,
            numeroPersonas = numeroPersonas,
            idCliente = idCliente,
            direccionEvento = direccionEvento,
            listaIdsEmpleados = listaIdsEmpleados,
            idMobiliario = idMobiliario,
            idServicio = idServicio,
            detalleServicio = detalleServicio
        )
        agregarEvento(evento, onSuccess, onFailure)
    }

    fun actualizarEvento(
        evento: Evento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.actualizarEvento(evento, {
            cargarEventos()
            onSuccess()
        }, onFailure)
    }

    fun obtenerEventoPorId(eventoId: String, onResult: (Evento?) -> Unit) {
        repository.obtenerEventoPorId(eventoId, onResult)
    }
}
