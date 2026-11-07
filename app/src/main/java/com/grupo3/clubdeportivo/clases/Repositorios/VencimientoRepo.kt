package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

class VencimientoRepo(private val dbHelper: DbHelper) {

    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {

        val listaCuotas = mutableListOf<CuotaDTO>()
        val db = dbHelper.readableDatabase
        var fechaVencimiento = FechaDb.formatearFechaHora(fecha)

        val query = """
            SELECT p.nombre, p.apellido, p.dni, p.email, 
            s.id AS id_socio, s.estado AS estado_socio,
            c.id AS id_cuota, c.monto_cuota, c.fecha_vencimiento, c.estado_cuota
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON cl.id = s.cliente_id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE date(c.fecha_vencimiento) = date(?)
        """.trimIndent()

        db.rawQuery(query, arrayOf(fechaVencimiento)).use { cursor ->

            while (cursor.moveToNext()) {
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: ""
                val idSocioVal = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio"))
                val estadoSocioVal =
                    cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1

                val socioObj = Socio(nombre, apellido, dni, email, true).apply {
                    idSocio = idSocioVal
                    estado = estadoSocioVal
                }

                val cuota = CuotaDTO().apply {
                    idCuota = cursor.getInt(cursor.getColumnIndexOrThrow("id_cuota"))
                    socio = socioObj
                    estadoSocio = estadoSocioVal
                    montoCuota = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                    estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota"))
                }
                listaCuotas.add(cuota)
            }
        }
        return listaCuotas
    }
}
