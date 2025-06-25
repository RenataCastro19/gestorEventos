package com.example.gestoreventos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.repository.EventoRepository
import kotlin.random.Random

class EventoViewModel : ViewModel() {
    private val repository = EventoRepository()

    fun agregarEvento(
        evento: Evento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.agregarEvento(evento, onSuccess, onFailure)
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
}
