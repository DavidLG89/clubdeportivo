package com.grupo3.clubdeportivo.clases.Helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FechaDb {
    private const val PATRON = "yyyy-MM-dd HH:mm:ss"
    private const val PATRONHORA = "HH:mm"
    private const val PATRONFECHA = "dd/MM/yyyy"

    fun formatearFechaHora(fecha: Date?) : String {
        return SimpleDateFormat(PATRON, Locale.US).format(fecha)
    }

    fun formatearHora(fecha: Date?) : String {
        return SimpleDateFormat(PATRONHORA, Locale.US).format(fecha)
    }

    fun formatearFecha(fecha: Date?) : String {
        return SimpleDateFormat(PATRONFECHA, Locale.US).format(fecha)
    }

    fun parsearFecha(texto: String) : Date? =

        try {
            SimpleDateFormat(PATRON, Locale.US).parse(texto)
        } catch (_: Exception) {
            null
        }
}