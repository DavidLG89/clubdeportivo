package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.Actividad

class ActividadRepo(private val conexionBDsqlite: conexionBDsqlite) : IListado<Actividad> {

    override fun obtenerLista(): List<Actividad> {
        val actividades = mutableListOf<Actividad>()
        val db = conexionBDsqlite.getConexionBDsqlite() ?: return actividades

        try {
            val query = "SELECT id, nombre, precio FROM actividades"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val actividad = Actividad().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                }
                actividades.add(actividad)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return actividades
    }
}
