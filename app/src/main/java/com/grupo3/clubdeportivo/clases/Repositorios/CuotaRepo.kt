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
                val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")) ?: ""

                val cuota = CuotaDTO().apply {
                    socio?.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    socio?.apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    socio?.dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                    socio?.idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    estadoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                    montoCuota = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                    fechaVencimiento = FechaDb.parsearFecha(fechaStr) ?: Date()
                    estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")) ?: ""
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
}
