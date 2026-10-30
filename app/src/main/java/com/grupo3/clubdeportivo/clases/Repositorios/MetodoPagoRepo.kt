package com.grupo3.clubdeportivo.clases.Repositorios

import android.content.ContentValues
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

class MetodoPagoRepo(private val dbHelper: DbHelper) : IListado<MetodoPago> {

    override fun obtenerLista(): List<MetodoPago> {
        val metodosPago = mutableListOf<MetodoPago>()
        val db = dbHelper.readableDatabase

        val query = "SELECT id, nombre FROM metodo_pago"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val metodoPago = MetodoPago().apply {
                id = cursor.getInt(0)
                nombre = cursor.getString(1)
            }
            metodosPago.add(metodoPago)
            cursor.close()
        }

        return metodosPago
    }
}
