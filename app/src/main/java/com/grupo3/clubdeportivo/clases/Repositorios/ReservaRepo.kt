package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservaRepo(private val dbHelper: DbHelper) {

    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {

        val query = """
            SELECT r.id AS id_reserva, r.estado, r.cliente_id AS id_cliente, p.nombre, p.apellido, p.dni, a.nombre AS actividad, a.precio, pr.fecha_hora
            FROM reservas r
            INNER JOIN programaciones pr ON r.programacion_id = pr.id
            INNER JOIN actividades a ON pr.actividad_id = a.id
            INNER JOIN clientes cl ON r.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE r.id = ?
        """.trimIndent()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            val db = dbHelper.readableDatabase
            db.rawQuery(query, arrayOf(idReserva.toString())).use { cursor ->


                if (cursor.moveToFirst()) {
                    val fhStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                    val fhDate = try {
                        sdf.parse(fhStr) ?: Date()
                    } catch (_: Exception) {
                        Date()
                    }

                    ReservaDTO().apply {
                        this.idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                        idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                        nombreCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                        apellidoCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                        actividad =
                            cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                        fechaHora = fhDate
                        estadoReserva =
                            cursor.getString(cursor.getColumnIndexOrThrow("estado")) ?: ""
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun buscarReservaPorDniYActividadRepo(dni: String, idActividad: Int): ReservaDTO? {
        val query = """
            SELECT r.id AS id_reserva, cl.id AS id_cliente, p.nombre, p.apellido, p.dni, a.id AS id_actividad, a.nombre AS actividad, a.precio, r.estado, pr.fecha_hora
            FROM reservas r
            INNER JOIN programaciones pr ON r.programacion_id = pr.id
            INNER JOIN actividades a ON pr.actividad_id = a.id
            INNER JOIN clientes cl ON r.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni = ? AND a.id = ?
            ORDER BY pr.fecha_hora
        """.trimIndent()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            val db = dbHelper.readableDatabase

            db.rawQuery(query, arrayOf(dni, idActividad.toString())).use { cursor ->

                if (cursor.moveToFirst()) {
                    val fhStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                    val fhDate = try {
                        sdf.parse(fhStr) ?: Date()
                    } catch (_: Exception) {
                        Date()
                    }

                    ReservaDTO().apply {
                        this.idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                        idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                        nombreCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                        apellidoCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                        this.dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                        this.idActividad =
                            cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad"))
                        actividad =
                            cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                        fechaHora = fhDate
                        estadoReserva =
                            cursor.getString(cursor.getColumnIndexOrThrow("estado")) ?: ""
                    }

                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Devuelve: > 0 id de la reserva | -1 sin programación | -2 sin cupos
     *           -3 ya reservó | -99 error
     */
    fun generarReservaRepo(idActividad: Int, clienteId: Int, fechaHora: Date): Int {
        val db = dbHelper.writableDatabase

        db.beginTransaction()   // exclusiva: nadie escribe hasta el final
        return try {
            // Una sola consulta: programación + cupos + ¿ya reservó? + ¿es socio?
            val sql = """
                SELECT p.id, p.cupos_disponibles,
                       EXISTS(SELECT 1 FROM reservas WHERE programacion_id = p.id AND cliente_id = ?),
                       EXISTS(SELECT 1 FROM socios WHERE cliente_id = ? AND estado = 1)
                FROM programaciones p
                WHERE p.actividad_id = ? AND p.fecha_hora = ?
                LIMIT 1
            """
            val args = arrayOf(
                clienteId.toString(), clienteId.toString(),
                idActividad.toString(), FechaDb.formatearFechaHora(fechaHora)
            )

            var programacionId = 0
            var esSocio = false

            db.rawQuery(sql, args).use { c ->
                if (!c.moveToFirst()) return -1
                programacionId = c.getInt(0)
                if (c.getInt(1) <= 0) return -2
                if (c.getInt(2) == 1) return -3
                esSocio = c.getInt(3) == 1
            }

            val cv = ContentValues().apply {
                put("programacion_id", programacionId)
                put("cliente_id", clienteId)
                put("fecha_reserva", FechaDb.formatearFechaHora(Date()))
                put("estado", if (esSocio) "Autorizada" else "Pendiente de pago")
            }
            val reservaId = db.insert("reservas", null, cv)
            if (reservaId == -1L) return -99

            if (esSocio) {
                db.execSQL(
                    "UPDATE programaciones SET cupos_disponibles = cupos_disponibles - 1 WHERE id = ?",
                    arrayOf(programacionId.toString())
                )
            }

            db.setTransactionSuccessful()
            reservaId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }
}
