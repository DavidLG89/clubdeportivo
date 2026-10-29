package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

class PagosRepo(private val dbHelper: DbHelper) {

    fun registrarPagoCuota(
        socio: Socio,
        montoAPagar: Double,
        medioPago: Int
    ): Int {
        val db = dbHelper.writableDatabase

        val idCuota = db.rawQuery(
            "SELECT id FROM cuotas WHERE socio_id = ? AND estado_pago = 'Pendiente' " +
                    "ORDER BY fecha_vencimiento LIMIT 1", arrayOf(socio.idSocio.toString())
        )
            .use { c ->
                if (c.moveToFirst()) c.getInt(0) else null
            } ?: return -2

        db.beginTransaction()
        return try {
            val fechaHoy = FechaDb.formatearFechaHora(Date())

            val pagos = ContentValues().apply {
                put("id_cuota", idCuota)
                put("fecha_pago", fechaHoy)
                put("monto_pago", montoAPagar)
                put("medio_pago", medioPago)
            }

            val pagoId = db.insert("pagos", null, pagos)
            if (pagoId == -1L) return -99

            db.update(
                "cuotas", ContentValues().apply { put("estado_pago", "Pagado") },
                "id = ?", arrayOf(idCuota.toString())
            )

            db.update(
                "socios", ContentValues().apply { put("estado_socio", 1) },
                "id = ?", arrayOf(socio.idSocio.toString())
            )

            db.setTransactionSuccessful()
            pagoId.toInt()

        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }

    fun registrarPagoActividad(
        idReserva: Int,
        montoAPagar: Double,
        medioPago: Int
    ): Int {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        return try {
            var programacionId = 0
            var estado = ""

            db.rawQuery(
                """
                SELECT r.programacion_id, r.estado, p.cupos_disponibles
                FROM reservas r
                INNER JOIN programaciones p ON r.programacion_id = p.id
                WHERE r.id = ?
            """.trimIndent(),
                arrayOf(idReserva.toString())
            ).use { c ->
                if (!c.moveToFirst()) return -2
                programacionId = c.getInt(0)
                estado = c.getString(1)

                if (c.getInt(2) <= 0) return -2
            }

            val valores = ContentValues().apply {
                put("id", idReserva)
                put("fecha_pago", FechaDb.formatearFechaHora(Date()))
                put("monto", montoAPagar)
                put("medio", medioPago)
            }

            val pagoId = db.insert("pagos", null, valores)

            if (pagoId == -1L) return -99

            db.update(
                "reservas", ContentValues().apply { put("estado", "Pagado") },
                "id = ?", arrayOf(programacionId.toString())
            )

            db.execSQL(
                "UPDATE programaciones SET cupos_disponibles = cupos_disponibles -1 WHERE id = ?",
                arrayOf(programacionId.toString())
            )

            db.setTransactionSuccessful()
            pagoId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }
}
