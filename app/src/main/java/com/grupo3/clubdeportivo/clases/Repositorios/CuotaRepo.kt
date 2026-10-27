package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class CuotaRepo(private val conexionDatabase: conexionBDsqlite) {

    fun obtenerCuotasRepo(dni: String): List<CuotaDTO> {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return emptyList()
        return ProcedimientosAlmacenados.obtenerCuotasPorDni(conn, dni)
    }
}
