package com.grupo3.clubdeportivo.clases.Repositorios

import android.util.Log
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

/**
 * Repositorio encargado del acceso directo a la base de datos para la consulta de vencimientos de cuotas.
 *
 * Ejecuta consultas compuestas (JOINs) entre las tablas de personas, clientes, socios y cuotas.
 */
class VencimientoRepo(private val dbHelper: DbHelper) {

    /**
     * Obtiene la lista de cuotas que vencen en una fecha determinada.
     *
     * @param fecha Objeto [Date] que especifica la fecha de vencimiento a consultar.
     * @return Lista de objetos [CuotaDTO] encontrados en la base de datos.
     */
    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {
        val listaCuotas = mutableListOf<CuotaDTO>()
        val fechaVencimiento = FechaDb.formatearFechaHora(fecha)

        // Consulta SQL con JOINs para recuperar la información completa del socio y la cuota
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

        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query, arrayOf(fechaVencimiento))

            // Recorrido de los registros devueltos por el cursor
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

                // Instanciación del objeto Socio con su ID asignado
                val socioObj = Socio(idSocioVal, nombreVal, apellidoVal, dniVal, emailVal, true, estadoSocioVal).apply {
                    idSocio = idSocioVal
                }

                // Creación del DTO de la cuota
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
