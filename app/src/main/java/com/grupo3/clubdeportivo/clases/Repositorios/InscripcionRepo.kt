package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql

class InscripcionRepo(private val conexionDatabase: ConexionMySql) {

    fun formalizarInscripcion(clienteId: Int, montoCuota: Double): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -99
        try {
            val stmt = conn.prepareCall("{call FormalizarInscripcion(?, ?, ?)}")
            stmt.setInt(1, clienteId)
            stmt.setDouble(2, montoCuota)
            stmt.registerOutParameter(3, java.sql.Types.INTEGER)

            stmt.execute()
            val res = stmt.getInt(3)
            stmt.close()
            conn.close()
            return res
        } catch (ex: Exception) {
            throw Exception("Error repo inscripción: " + ex.message, ex)
        }
    }
}
