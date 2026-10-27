package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class ClienteRepo(private val conexionDatabase: conexionBDsqlite) {

    fun registrarClienteRepo(
        nombre: String,
        apellido: String,
        dni: String,
        aptoFisico: Boolean,
        esSocio: Boolean
    ): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -1
        return ProcedimientosAlmacenados.registrarCliente(
            conn, nombre, apellido, dni, aptoFisico, esSocio
        )
    }

    fun buscarClientePorDniRepo(dni: String): Cliente? {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return null
        return ProcedimientosAlmacenados.buscarClientePorDni(conn, dni)
    }

    fun actualizarAptoFisico(cliente: Cliente): Boolean {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return false
        try {
            val query = "UPDATE clientes SET apto_fisico = 1 WHERE id = ?"
            val stmt = conn.compileStatement(query)
            stmt.bindLong(1, cliente.idCliente.toLong())
            val filasAfectadas = stmt.executeUpdateDelete()
            stmt.close()
            return filasAfectadas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun asignarANoSocio(cliente: Cliente): Int {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return -1
        try {
            val query = "INSERT INTO no_socios(cliente_id, acceso_diario) VALUES(?, 1)"
            val stmt = conn.compileStatement(query)
            stmt.bindLong(1, cliente.idCliente.toLong())
            val resultado = stmt.executeInsert()
            stmt.close()
            return resultado.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
}
