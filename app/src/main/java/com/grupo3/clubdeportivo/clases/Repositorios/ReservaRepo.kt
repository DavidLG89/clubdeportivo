package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import android.util.Log
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReservaRepo(private val dbHelper: DbHelper) {

    object CodigoReserva {
        const val SIN_PROGRAMACION = -1
        const val SIN_CUPOS = -2
        const val YA_RESERVADA = -3
        const val ERROR = -99
    }

    fun buscarReservasPorDni(dni: String): List<ReservaDTO> {

        val dniBuscado = dni
        val reservasCliente = mutableListOf<ReservaDTO>()

        val query = """
        SELECT r.id AS id_reserva, cl.id AS id_cliente, pr.id AS id_progr, pr.cupos_disponibles,
               a.id AS id_actividad, a.nombre AS actividad, a.precio, r.estado,
               p.nombre AS nombre_cliente, p.apellido AS apellido_cliente, pr.fecha_hora
        FROM reservas r
        INNER JOIN programaciones pr ON r.programacion_id = pr.id
        INNER JOIN actividades a ON pr.actividad_id = a.id
        INNER JOIN clientes cl ON r.cliente_id = cl.id
        INNER JOIN personas p ON cl.persona_id = p.id
        WHERE p.dni = ?
        AND r.estado IN ('Pendiente de pago', 'Autorizada', 'Pagada')
          AND pr.fecha_hora >= datetime('now', 'localtime')
    """.trimIndent()

        val db = dbHelper.readableDatabase

        db.rawQuery(query, arrayOf(dniBuscado, "Pendiente de pago")).use { cursor ->
            while (cursor.moveToNext()) {
                val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                val fechaDate = FechaDb.parsearFecha(fechaStr) ?: Date()

                val reserva = ReservaDTO().apply {
                    idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                    idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                    this.dni = dniBuscado
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente")) ?: ""
                    apellidoCliente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_cliente")) ?: ""
                    idProgramacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_progr"))
                    cuposDisponibles = cursor.getInt(cursor.getColumnIndexOrThrow("cupos_disponibles"))
                    idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad"))
                    actividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                    fechaHoraReserva = fechaDate
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    estadoReserva = cursor.getString(cursor.getColumnIndexOrThrow("estado")) ?: ""
                }
                reservasCliente.add(reserva)
            }
        }
        return reservasCliente
    }
    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {

        val query = """
            SELECT r.id AS id_reserva, r.estado, r.cliente_id AS id_cliente, p.nombre, p.apellido, p.dni, a.nombre AS actividad, a.precio, pr.id AS id_progr, pr.cupos_disponibles, pr.fecha_hora
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
                    val fhDate = FechaDb.parsearFecha(
                        cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                    ) ?: Date()

                    ReservaDTO().apply {
                        this.idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                        idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                        nombreCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                        apellidoCliente =
                            cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                        idProgramacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_progr"))
                        cuposDisponibles = cursor.getInt(cursor.getColumnIndexOrThrow("cupos_disponibles"))
                        actividad =
                            cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                        fechaHoraReserva = fhDate
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

    fun generarReservaRepo(actividad: Actividad, cliente: Cliente, fechaHora: Date): Int {
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        return try {

            val sql = """
                 SELECT p.id, p.cupos_disponibles,
                 EXISTS(SELECT 1 FROM reservas r WHERE r.programacion_id = p.id AND cliente_id = ?)
                    FROM programaciones p
                    WHERE p.actividad_id = ?
                    AND p.fecha_hora = ?
                    LIMIT 1;
            """
            val args = arrayOf(
                cliente.idCliente.toString(), actividad.id.toString(), FechaDb.formatearFechaHora(fechaHora)
            )

            var programacionId = 0
            var cuposDisponibles = 0


            db.rawQuery(sql, args).use { c ->
                if (!c.moveToFirst()) return CodigoReserva.SIN_PROGRAMACION
                programacionId = c.getInt(0)
                cuposDisponibles = c.getInt(1)
                if (cuposDisponibles <= 0) return CodigoReserva.SIN_CUPOS
                if (c.getInt(2) == 1) return CodigoReserva.YA_RESERVADA
            }

            val esSocio = cliente is Socio
            val fechaHora = FechaDb.formatearFechaHora(Date())

            val cv = ContentValues().apply {
                put("programacion_id", programacionId)
                put("cliente_id", cliente.idCliente)
                put("fecha_reserva", fechaHora)
                put("estado", if (esSocio) "Autorizada" else "Pendiente de pago")
            }
            val reservaId = db.insert("reservas", null, cv)
            if (reservaId == -1L) return CodigoReserva.ERROR

            if (esSocio) {
                db.execSQL(
                    "UPDATE programaciones SET cupos_disponibles = (cupos_disponibles - 1) WHERE id = ?",
                    arrayOf(programacionId.toString())
                )
            }

            db.setTransactionSuccessful()
            reservaId.toInt()
        } catch (e: Exception) {
            Log.e("Reserva", "Error al generar reserva: cliente=${cliente.idCliente} actividad=${actividad.id}", e)
            -99
        } finally {
            db.endTransaction()
        }
    }
}
