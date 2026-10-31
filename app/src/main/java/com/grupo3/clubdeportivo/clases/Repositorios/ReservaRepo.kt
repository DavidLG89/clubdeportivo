package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.clases.Modelos.Socio
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
    fun generarReservaRepo(actividad: Actividad, cliente: Cliente, fechaHora: String): Int {
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        return try {

            val sql = """
                SELECT p.id, p.cupos_disponibles,
                       EXISTS(SELECT 1 FROM reservas WHERE programacion_id = p.id AND cliente_id = ?)
                FROM programaciones p
                WHERE p.actividad_id = ? AND p.fecha_hora = ?
                LIMIT 1
            """
            val args = arrayOf(
                cliente.id.toString(),
                actividad.id.toString(), fechaHora
            )

            var programacionId = 0
            var cuposDisponibles = 0


            db.rawQuery(sql, args).use { c ->
                if (!c.moveToFirst()) return -1
                programacionId = c.getInt(0)
                cuposDisponibles = c.getInt(1)
                if (cuposDisponibles <= 0) return -2
                if (c.getInt(2) == 1) return -3
            }

            val esSocio = cliente is Socio

            val cv = ContentValues().apply {
                put("programacion_id", programacionId)
                put("cliente_id", cliente.id)
                put("fecha_hora", fechaHora)
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
