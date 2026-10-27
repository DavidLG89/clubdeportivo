package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import java.util.Date

class CuotaRepo(private val conexionDatabase: ConexionMySql) {

    fun obtenerCuotasRepo(dni: String): List<CuotaDTO> {
        val cuotas = mutableListOf<CuotaDTO>()
        val conn = conexionDatabase.getMySqlConnection() ?: return cuotas

        try {
            val stmt = conn.prepareCall("{call ObtenerCuotasPorDni(?)}")
            stmt.setString(1, dni)
            val reader = stmt.executeQuery()

            while (reader.next()) {
                val cuota = CuotaDTO().apply {
                    nombre = reader.getString("nombre") ?: ""
                    apellido = reader.getString("apellido") ?: ""
                    this.dni = reader.getString("dni") ?: ""
                    idSocio = reader.getInt("id")
                    estadoSocio = reader.getBoolean("estado_socio")
                    montoCuota = reader.getDouble("monto_cuota")
                    fechaVencimiento = reader.getTimestamp("fecha_vencimiento") ?: Date()
                    estadoCuota = reader.getString("estado_cuota") ?: ""
                }
                cuotas.add(cuota)
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cuotas
    }
}
