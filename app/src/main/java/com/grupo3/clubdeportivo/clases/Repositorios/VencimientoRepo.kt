package com.grupo3.clubdeportivo.clases.Repositorios

import android.util.Log
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

class VencimientoRepo(private val dbHelper: DbHelper) {

    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {
        val listaCuotas = mutableListOf<CuotaDTO>()
        val fechaVencimiento = FechaDb.formatearFechaHora(fecha)

        val query = """
            SELECT s.id AS id_socio, p.nombre, p.apellido, p.dni, p.email, c.id AS id_cuota, c.monto_cuota, c.fecha_vencimiento, c.estado_cuota, s.estado AS estado_socio
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON cl.id = s.cliente_id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE date(c.fecha_vencimiento) = date(?)
            GROUP BY s.id
        """.trimIndent()

        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query, arrayOf(fechaVencimiento))

            while (cursor.moveToNext()) {
                val idSocioVal = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio"))
                val nombreVal = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                val apellidoVal = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                val dniVal = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                val emailVal = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: ""
                val estadoSocioVal = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                val idCuotaVal = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
                val montoVal = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                val estadoCuotaVal = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")) ?: ""

                val socioObj = Socio(idSocioVal, nombreVal, apellidoVal, dniVal, emailVal, true, estadoSocioVal).apply {
                    idSocio = idSocioVal
                }

                val cuota = CuotaDTO().apply {
                    idCuota = idCuotaVal
                    socio = socioObj
                    estadoSocio = estadoSocioVal
                    montoCuota = montoVal
                    estadoCuota = estadoCuotaVal
                }
                listaCuotas.add(cuota)
            }
            cursor.close()
            listaCuotas
        } catch (e: Exception) {
            Log.e("VencimientoRepo", "Error al obtener vencimientos por fecha", e)
            listaCuotas
        }
    }
}
