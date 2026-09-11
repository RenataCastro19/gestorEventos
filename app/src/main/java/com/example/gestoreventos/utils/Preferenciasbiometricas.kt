package com.example.gestoreventos.utils

import android.content.Context

object PreferenciasBiometricas {
    private const val PREFS = "prefs_biometria"

    fun estaHabilitada(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean("biometria_$id", false)
    }

    fun habilitar(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometria_$id", true).apply()
    }

    fun deshabilitar(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometria_$id", false).apply()
    }

    // Guarda cuál fue el último usuario que inició sesión en este dispositivo, para saber
    // a quién ofrecerle el atajo de huella en la pantalla de login sin que tenga que
    // escribir su ID primero.
    fun guardarUltimoId(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString("ultimo_id", id).apply()
    }

    fun obtenerUltimoId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString("ultimo_id", null)
    }
}