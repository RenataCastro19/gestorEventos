package com.example.gestoreventos.utils

import com.example.gestoreventos.model.Evento
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    /**
     * Verifica si un evento ya pasó
     */
    fun isEventoPasado(evento: Evento): Boolean {
        if (evento.fecha.isEmpty()) return false

        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaEvento = formato.parse(evento.fecha)
            val hoy = Calendar.getInstance()
            hoy.set(Calendar.HOUR_OF_DAY, 0)
            hoy.set(Calendar.MINUTE, 0)
            hoy.set(Calendar.SECOND, 0)
            hoy.set(Calendar.MILLISECOND, 0)

            fechaEvento?.before(hoy.time) ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Convierte una fecha en formato dd/MM/yyyy a Long para ordenar
     * Retorna Long.MAX_VALUE si la fecha es inválida (para poner al final)
     */
    fun parseFechaParaOrdenar(fecha: String): Long {
        if (fecha.isEmpty()) return Long.MAX_VALUE

        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formato.parse(fecha)
            date?.time ?: Long.MAX_VALUE
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    /**
     * Formatea una fecha para mostrar de manera legible
     */
    fun formatearFecha(fecha: String): String {
        if (fecha.isEmpty()) return "Sin fecha"

        return try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
            val date = formatoEntrada.parse(fecha)
            date?.let { formatoSalida.format(it) } ?: fecha
        } catch (e: Exception) {
            fecha
        }
    }
    fun parseFecha(fecha: String): Calendar {
        return try {
            val partes = fecha.split("/")
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, partes[0].toInt())
                set(Calendar.MONTH, partes[1].toInt() - 1)
                set(Calendar.YEAR, partes[2].toInt())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            Calendar.getInstance()
        }
    }
}