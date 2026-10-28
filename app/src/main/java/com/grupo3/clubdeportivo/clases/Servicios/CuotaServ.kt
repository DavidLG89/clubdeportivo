package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo

data class ResultadoCuota(
    val ok: Boolean,
    val mensaje: String,
    val cuota: CuotaDTO?
)

class CuotaServ(private val socioRepo: CuotaRepo) {

    fun obtenerCuotaPendienteServ(dni: String): ResultadoCuota {
        val cuota = socioRepo.obtenerCuotasRepo(dni)
            .filter { it.estadoCuota == "Pendiente" }
            .minByOrNull { it.fechaVencimiento }

        if (cuota == null) {
            return ResultadoCuota(false, "Cuota no existe", null)
        }

        return ResultadoCuota(true, "Cuota pendiente obtenida con éxito", cuota)
    }
}
