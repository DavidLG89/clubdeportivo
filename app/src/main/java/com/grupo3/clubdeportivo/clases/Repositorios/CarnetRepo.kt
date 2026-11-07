package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import android.util.Log
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Carnet
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

class CarnetRepo(private val dbHelper: DbHelper) {

    fun procesarCarnetSocio(socio: Socio): Carnet? {
        val db = dbHelper.writableDatabase
        val hoy = FechaDb.formatearFechaIso(Date())
        val estadoInt = if (socio.estado) 1 else 0

        return try {
            var carnetId = -1
            val query = "SELECT id FROM carnet WHERE socio_id = ?"
            val cursor = db.rawQuery(query, arrayOf(socio.idSocio.toString()))

            if (cursor.moveToFirst()) {
                carnetId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                cursor.close()

                // Si ya tenia carnet actualiza el registro de tabla carnet:
                // Fecha de emision: hoy, estado: sale de la tabla socios.
                val valores = ContentValues().apply {
                    put("fecha_emision", hoy)
                    put("estado", estadoInt)
                }
                db.update("carnet", valores, "id = ?", arrayOf(carnetId.toString()))
            } else {
                cursor.close()

                // Si no tenia carnet crea el registro en la tabla carnet de la siguiente forma:
                // Fecha de emision: hoy, estado: sale de la tabla socios.
                val valores = ContentValues().apply {
                    put("socio_id", socio.idSocio)
                    put("fecha_emision", hoy)
                    put("estado", estadoInt)
                }
                carnetId = db.insert("carnet", null, valores).toInt()
            }

            if (carnetId > 0) {
                var estadoCarnet = socio.estado
                val cursorCarnet = db.rawQuery(
                    "SELECT id, socio_id, fecha_emision, estado FROM carnet WHERE id = ?",
                    arrayOf(carnetId.toString())
                )
                if (cursorCarnet.moveToFirst()) {
                    val estInt = cursorCarnet.getInt(cursorCarnet.getColumnIndexOrThrow("estado"))
                    val fechaEmision = cursorCarnet.getString(cursorCarnet.getColumnIndexOrThrow("fecha_emision")) ?: hoy
                    estadoCarnet = (estInt == 1)
                    cursorCarnet.close()
                    Carnet(carnetId, socio, fechaEmision, estadoCarnet)
                } else {
                    cursorCarnet.close()
                    Carnet(carnetId, socio, hoy, socio.estado)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CarnetRepo", "Error al procesar carnet para socio_id ${socio.idSocio}", e)
            null
        }
    }
}
