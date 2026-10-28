package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados
import java.util.Date

class VencimientoRepo(private val conexionDatabase: conexionBDsqlite) {

    fun obtenerVencimientosPorFecha(fecha: Date): List<CuotaDTO> {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return emptyList()
        return ProcedimientosAlmacenados.listarVencimientos(conn, fecha)
    }
}
