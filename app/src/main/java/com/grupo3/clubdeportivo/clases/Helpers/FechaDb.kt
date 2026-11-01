package com.grupo3.clubdeportivo.clases.Helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FechaDb {
    private const val PATRON = "yyyy-MM-dd HH:mm:ss"
    private const val PATRONFECHA_ISO = "yyyy-MM-dd"
    private const val PATRONHORA = "HH:mm"
    private const val PATRONFECHA = "dd/MM/yyyy"

    fun formatearFechaHora(fecha: Date?) : String {
        return SimpleDateFormat(PATRON, Locale.US).format(fecha ?: Date())
    }

    fun formatearFechaIso(fecha: Date?) : String {
        return SimpleDateFormat(PATRONFECHA_ISO, Locale.US).format(fecha ?: Date())
    }

    fun formatearHora(fecha: Date?) : String {
        return SimpleDateFormat(PATRONHORA, Locale.US).format(fecha ?: Date())
    }

    fun formatearFecha(fecha: Date?) : String {
        return SimpleDateFormat(PATRONFECHA, Locale.US).format(fecha ?: Date())
    }

    fun parsearFecha(texto: String) : Date? {
        if (texto.isBlank()) return null
        val patrones = arrayOf(PATRON, PATRONFECHA_ISO, PATRONFECHA)
        for (patron in patrones) {
            try {
                return SimpleDateFormat(patron, Locale.US).parse(texto)
            } catch (_: Exception) {}
        }
        return null
    }
}