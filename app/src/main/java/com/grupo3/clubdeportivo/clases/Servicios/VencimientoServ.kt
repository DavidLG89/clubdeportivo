package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.VencimientoRepo
import java.util.Date

data class ResultadoVenc(
    val ok: Boolean,
    val mensaje: String,
    val cuotas: List<CuotaDTO> = emptyList()
)
class VencimientoServ(private val repo: VencimientoRepo) {

    fun obtenerPendientesPorFecha(fecha: Date) : ResultadoVenc {
        val cuotasPendientes = repo.obtenerVencimientosPorFecha(fecha)
            .filter { it.estadoCuota == "Pendiente"}

        if(cuotasPendientes.isEmpty()) {
            return ResultadoVenc(false, "No existen cuotas pendientes de pago")
        }

        return ResultadoVenc(true, "Cuotas obtenidas exitosamente", cuotasPendientes)
    }
}