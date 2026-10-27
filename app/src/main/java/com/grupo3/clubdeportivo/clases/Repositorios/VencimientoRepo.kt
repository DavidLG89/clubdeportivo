package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import java.util.Date

class VencimientoRepo(private val conexionDatabase: ConexionMySql) {

    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {
        val lista = mutableListOf<CuotaDTO>()
        val conn = conexionDatabase.getMySqlConnection() ?: return lista

        try {
            val stmt = conn.prepareCall("{call ListarVencimientos(?)}")
            stmt.setDate(1, java.sql.Date(fecha.time))
            val reader = stmt.executeQuery()

            while (reader.next()) {
                val cuota = CuotaDTO().apply {
                    nombre = reader.getString("nombre") ?: ""
                    apellido = reader.getString("apellido") ?: ""
                    dni = reader.getString("dni") ?: ""
                    idSocio = reader.getInt("id")
                    montoCuota = reader.getDouble("monto_cuota")
                    fechaVencimiento = reader.getTimestamp("fecha_vencimiento") ?: Date()
                    estadoCuota = reader.getString("estado_cuota") ?: ""
                }
                lista.add(cuota)
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }
}
