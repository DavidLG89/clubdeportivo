package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.VencimientoRepo
import java.util.Date

class VencimientoServ(private val repo: VencimientoRepo) {

    fun obtenerPendientesPorFecha(fecha: Date) : List<CuotaDTO> {
        return repo.obtenerVencimientosPorFecha(fecha)
            .filter { it.estadoCuota == "Pendiente"}
    }
}