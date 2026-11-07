package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo

data class ResultadoCuota(
    val ok: Boolean,
    val mensaje: String,
    val cuotas: List<CuotaDTO>
)

class CuotaServ(private val socioRepo: CuotaRepo) {

    fun obtenerCuotasServ(dni: String): ResultadoCuota {
        val cuotas = socioRepo.obtenerCuotasRepo(dni)

        return ResultadoCuota(true, "Cuota pendiente obtenida con éxito", cuotas)
    }

    fun registrarCuotasSocio(socioId: Int, montoCuota: Double): Boolean {
        return socioRepo.registrarCuotasSocio(socioId, montoCuota)
    }
}
