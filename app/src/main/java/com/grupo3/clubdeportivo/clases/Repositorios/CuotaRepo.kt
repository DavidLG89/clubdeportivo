package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CuotaRepo(private val dbHelper: DbHelper) {

    fun obtenerCuotasRepo(dni: String): List<CuotaDTO> {

        val query = """
            SELECT p.nombre, p.apellido, p.dni, s.id, s.estado AS estado_socio, c.id AS id_cuota, c.monto_cuota, c.estado_cuota, c.fecha_vencimiento
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON cl.id = s.cliente_id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE p.dni = ?
        """.trimIndent()

        val list = mutableListOf<CuotaDTO>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query, arrayOf(dni))
            while (cursor.moveToNext()) {
                val idCuotaVal = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
                val idSocioVal = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombreVal = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                val apellidoVal = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                val dniVal = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                val estadoSocioVal = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                val montoVal = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")) ?: ""
                val estadoCuotaVal = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")) ?: ""

                val socioObj = com.grupo3.clubdeportivo.clases.Modelos.Socio(
                    nombreVal, apellidoVal, dniVal, "", true
                ).apply {
                    idSocio = idSocioVal
                    estado = estadoSocioVal
                }

                val cuota = CuotaDTO().apply {
                    idCuota = idCuotaVal
                    socio = socioObj
                    estadoSocio = estadoSocioVal
                    montoCuota = montoVal
                    fechaVencimiento = FechaDb.parsearFecha(fechaStr) ?: Date()
                    estadoCuota = estadoCuotaVal
                }
                list.add(cuota)
            }
            cursor.close()
            list
        } catch (e: Exception) {
            e.printStackTrace()
            list
        }
    }

    fun registrarCuotasSocio(socioId: Int, montoCuota: Double): Boolean {
        val db = dbHelper.writableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = java.util.Calendar.getInstance()

        return try {
            db.beginTransaction()
            try {
                // 1. Cuota de inscripción (vencimiento hoy)
                val fechaInscripcion = sdf.format(calendar.time)
                val cvInscripcion = android.content.ContentValues().apply {
                    put("socio_id", socioId)
                    put("monto_cuota", montoCuota)
                    put("fecha_vencimiento", fechaInscripcion)
                    put("estado_cuota", "Pendiente")
                }
                db.insert("cuotas", null, cvInscripcion)

                // 2. 6 cuotas adicionales con vencimientos cada 30 días contados desde hoy
                for (i in 1..6) {
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 30)
                    val fechaVenc = sdf.format(calendar.time)
                    val cvMensual = android.content.ContentValues().apply {
                        put("socio_id", socioId)
                        put("monto_cuota", montoCuota)
                        put("fecha_vencimiento", fechaVenc)
                        put("estado_cuota", "Pendiente")
                    }
                    db.insert("cuotas", null, cvMensual)
                }

                db.setTransactionSuccessful()
                true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            android.util.Log.e("CuotaRepo", "Error al registrar cuotas de socio", e)
            false
        }
    }
}
