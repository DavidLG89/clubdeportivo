package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class InscripcionRepo(private val dbHelper: DbHelper) {

    fun formalizarInscripcion(cliente: Cliente, montoCuota: Double): Int {
        val db = dbHelper.writableDatabase
        val inscripcion = db.rawQuery("SELECT id FROM socios WHERE cliente_id = ?", arrayOf(cliente.idCliente.toString()))
            .use {

            }
    }
}
