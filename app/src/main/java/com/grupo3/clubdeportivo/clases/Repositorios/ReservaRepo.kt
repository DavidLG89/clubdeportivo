package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados
import java.util.Date

class ReservaRepo(private val conexionDatabase: conexionBDsqlite) {

    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return null
        return ProcedimientosAlmacenados.buscarReservaPorId(conn, idReserva)
    }

    fun buscarReservaPorDniYActividadRepo(dni: String, idActividad: Int): ReservaDTO? {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return null
        return ProcedimientosAlmacenados.buscarReservaPorDniYActividad(conn, dni, idActividad)
    }

    fun generarReservaRepo(idActividad: Int, clienteId: Int, fechaHora: Date): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -1
        return ProcedimientosAlmacenados.generarReserva(conn, idActividad, clienteId, fechaHora)
    }
}
