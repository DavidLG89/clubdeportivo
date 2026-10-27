package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.Actividad

class ActividadRepo(private val conexionMysql: ConexionMySql) : IListado<Actividad> {

    override fun obtenerLista(): List<Actividad> {
        val actividades = mutableListOf<Actividad>()
        val conn = conexionMysql.getMySqlConnection() ?: return actividades

        try {
            val query = "SELECT id, nombre, precio FROM actividades"
            val stmt = conn.prepareStatement(query)
            val reader = stmt.executeQuery()

            while (reader.next()) {
                val actividad = Actividad().apply {
                    id = reader.getInt("id")
                    nombre = reader.getString("nombre")
                    precio = reader.getDouble("precio")
                }
                actividades.add(actividad)
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return actividades
    }
}
