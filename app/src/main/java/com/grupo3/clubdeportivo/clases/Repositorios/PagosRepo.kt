package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class PagosRepo(private val conexionDatabase: conexionBDsqlite) {

    fun registrarPagoCuota(
        idSocio: Int,
        montoAPagar: Double,
        conceptoPago: Int,
        medioPago: Int
    ): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -1
        return ProcedimientosAlmacenados.registrarPagoCuota(
            conn, idSocio, montoAPagar, conceptoPago, medioPago
        )
    }

    fun registrarPagoActividad(
        idReserva: Int,
        montoAPagar: Double,
        conceptoPago: Int,
        medioPago: Int
    ): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -1
        return ProcedimientosAlmacenados.registrarPagoActividad(
            conn, idReserva, montoAPagar, conceptoPago, medioPago
        )
    }
}
