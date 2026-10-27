package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.ConceptoPago

class ConceptoPagoRepo(private val conexionDatabase: ConexionMySql) : IListado<ConceptoPago> {

    override fun obtenerLista(): List<ConceptoPago> {
        val lista = mutableListOf<ConceptoPago>()
        val conn = conexionDatabase.getMySqlConnection() ?: return lista

        try {
            val query = "SELECT id, nombre FROM conceptos_pago"
            val stmt = conn.prepareStatement(query)
            val dr = stmt.executeQuery()

            while (dr.next()) {
                val concepto = ConceptoPago().apply {
                    id = dr.getInt("id")
                    nombre = dr.getString("nombre") ?: ""
                }
                lista.add(concepto)
            }
            dr.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }
}
