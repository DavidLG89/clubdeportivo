package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgramacionRepo(private val conexionDatabase: conexionBDsqlite) {

    fun buscarProgramacionPorActividad(idActividad: Int): List<Programacion> {
        val listaProgramacion = mutableListOf<Programacion>()
        val db = conexionDatabase.getConexionBDsqlite() ?: return listaProgramacion
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val query = "SELECT a.id AS id_act, p.fecha_hora, p.id AS id_prog, p.cupos_disponibles" +
                " FROM programaciones p INNER JOIN actividades a ON a.id = p.actividad_id " +
                " WHERE a.id = ?" +
                " ORDER BY p.fecha_hora ASC"

        try {
            val cursor = db.rawQuery(query, arrayOf(idActividad.toString()))

            while (cursor.moveToNext()) {
                val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                val fechaDate = try { sdf.parse(fechaStr) ?: Date() } catch (_: Exception) { Date() }

                val programacion = Programacion().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_prog"))
                    fechaHora = fechaDate
                    cuposDisponibles = cursor.getInt(cursor.getColumnIndexOrThrow("cupos_disponibles"))
                }
                listaProgramacion.add(programacion)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return listaProgramacion
    }
}
