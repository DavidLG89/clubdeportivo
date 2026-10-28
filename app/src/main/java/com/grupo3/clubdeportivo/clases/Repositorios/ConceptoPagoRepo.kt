package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.ConceptoPago

class ConceptoPagoRepo(private val conexionDatabase: conexionBDsqlite) : IListado<ConceptoPago> {

    override fun obtenerLista(): List<ConceptoPago> {
        val lista = mutableListOf<ConceptoPago>()
        val db = conexionDatabase.getConexionBDsqlite() ?: return lista

        try {
            val query = "SELECT id, nombre FROM conceptos_pago"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val concepto = ConceptoPago().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                }
                lista.add(concepto)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }
}
