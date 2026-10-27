package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import java.util.Date

class ReservaRepo(private val conexionDatabase: ConexionMySql) {

    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {
        val conn = conexionDatabase.getMySqlConnection() ?: return null
        var reserva: ReservaDTO? = null

        try {
            val stmt = conn.prepareCall("{call BuscarReservaPorId(?)}")
            stmt.setInt(1, idReserva)
            val reader = stmt.executeQuery()

            if (reader.next()) {
                reserva = ReservaDTO().apply {
                    this.idReserva = reader.getInt("id_reserva")
                    idCliente = reader.getInt("id_cliente")
                    nombreCliente = reader.getString("nombre") ?: ""
                    apellidoCliente = reader.getString("apellido") ?: ""
                    dni = reader.getString("dni") ?: ""
                    actividad = reader.getString("actividad") ?: ""
                    precio = reader.getDouble("precio")
                    fechaHora = reader.getTimestamp("fecha_hora") ?: Date()
                    estadoReserva = reader.getString("estado") ?: ""
                }
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return reserva
    }

    fun buscarReservaPorDniYActividadRepo(dni: String, idActividad: Int): ReservaDTO? {
        val conn = conexionDatabase.getMySqlConnection() ?: return null
        var reserva: ReservaDTO? = null

        try {
            val stmt = conn.prepareCall("{call BuscarReservaPorDniYActividad(?, ?)}")
            stmt.setString(1, dni)
            stmt.setInt(2, idActividad)
            val reader = stmt.executeQuery()

            if (reader.next()) {
                reserva = ReservaDTO().apply {
                    this.idReserva = reader.getInt("id_reserva")
                    idCliente = reader.getInt("id_cliente")
                    nombreCliente = reader.getString("nombre") ?: ""
                    apellidoCliente = reader.getString("apellido") ?: ""
                    this.dni = reader.getString("dni") ?: ""
                    this.idActividad = reader.getInt("id_actividad")
                    actividad = reader.getString("actividad") ?: ""
                    precio = reader.getDouble("precio")
                    fechaHora = reader.getTimestamp("fecha_hora") ?: Date()
                    estadoReserva = reader.getString("estado") ?: ""
                }
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return reserva
    }

    fun generarReservaRepo(idActividad: Int, clienteId: Int, fechaHora: Date): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -1
        try {
            val stmt = conn.prepareCall("{call GenerarReserva(?, ?, ?, ?)}")
            stmt.setInt(1, idActividad)
            stmt.setInt(2, clienteId)
            stmt.setTimestamp(3, java.sql.Timestamp(fechaHora.time))
            stmt.registerOutParameter(4, java.sql.Types.INTEGER)

            stmt.execute()
            val res = stmt.getInt(4)
            stmt.close()
            conn.close()
            return res
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
}
