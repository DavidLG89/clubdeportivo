package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class InscripcionRepo(private val conexionDatabase: conexionBDsqlite) {

    fun formalizarInscripcion(clienteId: Int, montoCuota: Double): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -99
        return ProcedimientosAlmacenados.formalizarInscripcion(conn, clienteId, montoCuota)
    }
}
