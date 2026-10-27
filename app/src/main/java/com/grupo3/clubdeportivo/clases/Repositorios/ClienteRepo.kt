package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.NoSocio
import com.grupo3.clubdeportivo.clases.Modelos.Socio

class ClienteRepo(private val conexionDatabase: ConexionMySql) {

    fun registrarClienteRepo(
        nombre: String,
        apellido: String,
        dni: String,
        aptoFisico: Boolean,
        esSocio: Boolean
    ): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -1
        try {
            val stmt = conn.prepareCall("{call RegistrarCliente(?, ?, ?, ?, ?, ?)}")
            stmt.setString(1, nombre)
            stmt.setString(2, apellido)
            stmt.setString(3, dni)
            stmt.setBoolean(4, aptoFisico)
            stmt.setBoolean(5, esSocio)
            stmt.registerOutParameter(6, java.sql.Types.INTEGER)

            stmt.execute()
            val resultado = stmt.getInt(6)
            stmt.close()
            conn.close()
            return resultado
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    fun buscarClientePorDniRepo(dni: String): Cliente? {
        val conn = conexionDatabase.getMySqlConnection() ?: return null
        var cliente: Cliente? = null

        try {
            val stmt = conn.prepareCall("{call BuscarClientePorDni(?)}")
            stmt.setString(1, dni)
            val reader = stmt.executeQuery()

            if (reader.next()) {
                val idCliente = reader.getInt("id_cliente")
                val nombre = reader.getString("nombre") ?: ""
                val apellido = reader.getString("apellido") ?: ""
                val dniVal = reader.getString("dni") ?: ""
                val aptoFisico = reader.getBoolean("apto_fisico")
                val tipo = reader.getString("tipo_cliente") ?: ""

                cliente = when (tipo) {
                    "Socio" -> {
                        val idSocio = reader.getInt("id_socio")
                        val estado = reader.getBoolean("estado_socio")
                        Socio(idCliente, nombre, apellido, dniVal, aptoFisico, estado).apply {
                            this.idSocio = idSocio
                        }
                    }
                    "NoSocio" -> {
                        val idNoSocio = reader.getInt("id_no_socio")
                        val accesoDiario = reader.getBoolean("acceso_diario")
                        NoSocio(idCliente, nombre, apellido, dniVal, aptoFisico).apply {
                            this.idNoSocio = idNoSocio
                            this.accesoDiario = accesoDiario
                        }
                    }
                    else -> {
                        Cliente(idCliente, nombre, apellido, dniVal, aptoFisico)
                    }
                }
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cliente
    }

    fun actualizarAptoFisico(cliente: Cliente): Boolean {
        val conn = conexionDatabase.getMySqlConnection() ?: return false
        try {
            val query = "UPDATE clientes SET apto_fisico = true WHERE id = ?"
            val stmt = conn.prepareStatement(query)
            stmt.setInt(1, cliente.idCliente)
            val filasAfectadas = stmt.executeUpdate()
            stmt.close()
            conn.close()
            return filasAfectadas > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun asignarANoSocio(cliente: Cliente): Int {
        val conn = conexionDatabase.getMySqlConnection() ?: return -1
        try {
            val query = "INSERT INTO no_socios(cliente_id, acceso_diario) VALUES(?, true)"
            val stmt = conn.prepareStatement(query)
            stmt.setInt(1, cliente.idCliente)
            val resultado = stmt.executeUpdate()
            stmt.close()
            conn.close()
            return resultado
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
}
