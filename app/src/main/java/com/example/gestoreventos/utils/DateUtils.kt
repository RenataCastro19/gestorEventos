package com.example.gestoreventos.utils

import com.example.gestoreventos.model.Evento
import java.util.*

object DateUtils {

    /**
     * Parsea una fecha en formato "DD/MM/YYYY" a un objeto Calendar
     */
    fun parseFecha(fecha: String): Calendar {
        return try {
            val partes = fecha.split("/")
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, partes[0].toInt())
            calendar.set(Calendar.MONTH, partes[1].toInt() - 1)
            calendar.set(Calendar.YEAR, partes[2].toInt())
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar
        } catch (e: Exception) {
            Calendar.getInstance()
        }
    }

    /**
     * Verifica si un evento es pasado comparando su fecha con la fecha actual
     */
    fun isEventoPasado(evento: Evento): Boolean {
        return try {
            val eventoDate = parseFecha(evento.fecha)
            val hoy = Calendar.getInstance()
            hoy.set(Calendar.HOUR_OF_DAY, 0)
            hoy.set(Calendar.MINUTE, 0)
            hoy.set(Calendar.SECOND, 0)
            hoy.set(Calendar.MILLISECOND, 0)

            eventoDate.before(hoy)
        } catch (e: Exception) {
            false
        }
    }
}