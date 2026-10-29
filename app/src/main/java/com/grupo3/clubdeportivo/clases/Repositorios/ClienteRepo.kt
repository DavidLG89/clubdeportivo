package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import android.util.Log
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.NoSocio
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import androidx.core.database.sqlite.transaction

class ClienteRepo(private val dbHelper: DbHelper) {

        companion object {
            const val DNI_EXISTENTE = -1
            const val ERROR = -2
        }

        fun registrarCliente(
            nombre: String,
            apellido: String,
            dni: String,
            email: String,
            aptoFisico: Boolean,
            esSocio: Boolean
        ): Int {
            val db = dbHelper.writableDatabase
            db.transaction {

                try {
                    // El chequeo va DENTRO de la transacción
                    val existe = rawQuery("SELECT 1 FROM personas WHERE dni = ?", arrayOf(dni))
                        .use { it.moveToFirst() }
                    if (existe) return DNI_EXISTENTE

                    val cvPersona = ContentValues().apply {
                        put("nombre", nombre)
                        put("apellido", apellido)
                        put("dni", dni)
                        put("email", email)
                    }
                    val personaId = insert("personas", null, cvPersona)
                    if (personaId == -1L) throw Exception("Error al insertar persona")

                    val cvCliente = ContentValues().apply {
                        put("persona_id", personaId)
                        put("apto_fisico", if (aptoFisico) 1 else 0)
                    }
                    val clienteId = insert("clientes", null, cvCliente)
                    if (clienteId == -1L) throw Exception("Error al insertar cliente")

                    if (!esSocio) {
                        val cvNoSocio = ContentValues().apply {
                            put("cliente_id", clienteId)
                            put("acceso_diario", 1)
                        }
                        if (insert("no_socios", null, cvNoSocio) == -1L)
                            throw Exception("Error al insertar no socio")
                    }

                    return clienteId.toInt()
                } catch (e: Exception) {
                    Log.e("ClienteRepo", "No se pudo registrar el cliente", e)
                    return ERROR
                } finally {
                }
            }
        }

    fun buscarClientePorDniRepo(dni: String): Cliente? {
        val query = """
            SELECT cl.id AS id_cliente, p.nombre, p.apellido, p.dni, p.email, cl.apto_fisico,
                   CASE 
                       WHEN s.id IS NOT NULL THEN 'Socio'
                       WHEN ns.id IS NOT NULL THEN 'NoSocio'
                       ELSE 'Cliente'
                   END AS tipo_cliente,
                   s.id AS id_socio, s.estado AS estado_socio, ns.id AS id_no_socio, ns.acceso_diario
            FROM clientes cl
            JOIN personas p ON p.id = cl.persona_id
            LEFT JOIN socios s ON s.cliente_id = cl.id
            LEFT JOIN no_socios ns ON ns.cliente_id = cl.id
            WHERE p.dni = ?
        """.trimIndent()

        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query, arrayOf(dni))
            var cliente: Cliente? = null

            if (cursor.moveToFirst()) {
                val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                val dniVal = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("apto_fisico")) == 1
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")) ?: ""

                cliente = when (tipo) {
                    "Socio" -> {
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio"))
                        val estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                        Socio(idCliente, nombre, apellido, dniVal, email, aptoFisico, estado).apply {
                            this.idSocio = idSocio
                        }
                    }
                    "NoSocio" -> {
                        val idNoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id_no_socio"))
                        val accesoDiario = cursor.getInt(cursor.getColumnIndexOrThrow("acceso_diario")) == 1
                        NoSocio(idCliente, nombre, apellido, dniVal, email, aptoFisico).apply {
                            this.idNoSocio = idNoSocio
                            this.accesoDiario = accesoDiario
                        }
                    }
                    else -> Cliente(idCliente, nombre, apellido, dniVal, email, aptoFisico)
                }
            }
            cursor.close()
            cliente
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun actualizarAptoFisico(cliente: Cliente): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply { put(
            "apto_fisico", 1
        ) }

            val filasAfectadas = db.update(
                "clientes",
                valores,
                "id=?",
                arrayOf(cliente.idCliente.toString())

            )
        return filasAfectadas > 0
    }

   fun asignarANoSocio(cliente: Cliente): Int {
      val db = dbHelper.writableDatabase
          val valores = ContentValues().apply {
              put("cliente_id", cliente.idCliente)
              put("acceso_diario", 1)
          }

       return dbHelper.writableDatabase
           .insertOrThrow("no_socios", null, valores)
           .toInt()
   }

}