package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import java.util.Date

class ProgramacionRepo(private val conexionDatabase: ConexionMySql) {

    fun buscarProgramacionPorActividad(idActividad: Int): List<Programacion> {
        val listaProgramacion = mutableListOf<Programacion>()
        val conn = conexionDatabase.getMySqlConnection() ?: return listaProgramacion

        val query = "SELECT a.id AS id_act, p.fecha_hora, p.id AS id_prog, p.cupos_disponibles" +
                " FROM programaciones p INNER JOIN actividades a ON a.id = p.actividad_id " +
                " WHERE a.id = ?" +
                " ORDER BY p.fecha_hora ASC"

        try {
            val stmt = conn.prepareStatement(query)
            stmt.setInt(1, idActividad)
            val reader = stmt.executeQuery()

            while (reader.next()) {
                val programacion = Programacion().apply {
                    id = reader.getInt("id_prog")
                    fechaHora = reader.getTimestamp("fecha_hora") ?: Date()
                    cuposDisponibles = reader.getInt("cupos_disponibles")
                }
                listaProgramacion.add(programacion)
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return listaProgramacion
    }
}
