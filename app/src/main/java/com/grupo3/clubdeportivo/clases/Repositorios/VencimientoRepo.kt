package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import java.util.Date

class VencimientoRepo(private val dbHelper: DbHelper) {

    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {

        val listaCuotas = mutableListOf<CuotaDTO>()
        val db = dbHelper.readableDatabase
        var fechaVencimiento = FechaDb.formatearFechaHora(fecha)

        val query = """
            SELECT p.nombre, p.apellido, p.dni, p.email, c.monto_cuota, c.fecha_vencimiento, c.estado_cuota
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON cl.id = s.cliente_id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE data(c.fecha_vencimiento) = date(?)
        """.trimIndent()

       val cursor = db.rawQuery(query, arrayOf(fechaVencimiento))

        while (cursor.moveToNext()) {
            cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))

            val cuota = CuotaDTO().apply {
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: ""
                estadoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                montoCuota = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))
                estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota"))
            }
        listaCuotas.add(cuota)
        }
        return listaCuotas
    }
}
