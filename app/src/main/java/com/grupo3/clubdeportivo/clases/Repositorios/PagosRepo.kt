package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql

class PagosRepo(private val conexionDatabase: ConexionMySql) {

    fun registrarPagoCuota(
        idSocio: Int,
        montoAPagar: Double,
        conceptoPago: Int,
        medioPago: Int
    ): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -1
        try {
            val stmt = conn.prepareCall("{call RegistrarPagoCuota(?, ?, ?, ?, ?)}")
            stmt.setInt(1, idSocio)
            stmt.setDouble(2, montoAPagar)
            stmt.setInt(3, conceptoPago)
            stmt.setInt(4, medioPago)
            stmt.registerOutParameter(5, java.sql.Types.INTEGER)

            stmt.execute()
            val res = stmt.getInt(5)
            stmt.close()
            conn.close()
            return res
        } catch (ex: Exception) {
            throw Exception("Error repo pago cuota: " + ex.message, ex)
        }
    }

    fun registrarPagoActividad(
        idReserva: Int,
        montoAPagar: Double,
        conceptoPago: Int,
        medioPago: Int
    ): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -1
        try {
            val stmt = conn.prepareCall("{call RegistrarPagoActividad(?, ?, ?, ?, ?)}")
            stmt.setDouble(1, montoAPagar)
            stmt.setInt(2, idReserva)
            stmt.setInt(3, conceptoPago)
            stmt.setInt(4, medioPago)
            stmt.registerOutParameter(5, java.sql.Types.INTEGER)

            stmt.execute()
            val res = stmt.getInt(5)
            stmt.close()
            conn.close()
            return res
        } catch (ex: Exception) {
            throw Exception("Error repo pago actividad: " + ex.message, ex)
        }
    }
}
