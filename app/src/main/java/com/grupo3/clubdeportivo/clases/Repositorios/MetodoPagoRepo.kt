package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

class MetodoPagoRepo(private val conexionBDsqlite: conexionBDsqlite) : IListado<MetodoPago> {

    override fun obtenerLista(): List<MetodoPago> {
        val lista = mutableListOf<MetodoPago>()
        val db = conexionBDsqlite.getConexionBDsqlite() ?: return lista

        try {
            val query = "SELECT id, nombre FROM metodos_pago"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val metodo = MetodoPago().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                }
                lista.add(metodo)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }
}
