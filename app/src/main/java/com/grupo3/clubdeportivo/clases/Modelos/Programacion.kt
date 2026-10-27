package com.grupo3.clubdeportivo.clases.Modelos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Programacion {
    var id: Int = 0
    var actividad: Actividad? = null
    var fechaHora: Date = Date()
    var cuposDisponibles: Int = 0

    val descripcion: String
        get() {
            if (id == 0) return "Seleccione fecha y horario"
            return formatoFechaHora()
        }

    fun formatoFechaHora(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(fechaHora)
    }

    fun estaDisponible(): Boolean {
        return cuposDisponibles > 0
    }

    fun descontarCupo() {
        if (cuposDisponibles <= 0) {
            throw Exception("No hay cupos disponibles")
        }
        cuposDisponibles--
    }

    fun estaVigente(): Boolean {
        return fechaHora.after(Date())
    }
}
