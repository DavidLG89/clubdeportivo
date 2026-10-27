package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

class MetodoPagoRepo(private val conexionMysql: ConexionMySql) : IListado<MetodoPago> {

    override fun obtenerLista(): List<MetodoPago> {
        val lista = mutableListOf<MetodoPago>()
        val conn = conexionMysql.getMySqlConnection() ?: return lista

        try {
            val query = "SELECT id, nombre FROM metodos_pago"
            val stmt = conn.prepareStatement(query)
            val dr = stmt.executeQuery()

            while (dr.next()) {
                val metodo = MetodoPago().apply {
                    id = dr.getInt("id")
                    nombre = dr.getString("nombre") ?: ""
                }
                lista.add(metodo)
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
