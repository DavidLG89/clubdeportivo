package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo

data class ResultadoCuota(
    val ok: Boolean,
    val mensaje: String,
    val cuotas: List<CuotaDTO>
)

class CuotaServ(private val cuotaRepo: CuotaRepo, private val clienteRepo: ClienteRepo) {

    fun obtenerCuotasSocioServ(dni: String): ResultadoCuota {
        val cliente = clienteRepo.buscarClientePorDniRepo(dni)

        if (cliente !is Socio) {
            return ResultadoCuota(false, "DNI no corresponde a un socio", emptyList())
        }

        val cuotas = cuotaRepo.obtenerCuotasRepo(dni)

        if (cuotas.isEmpty()) {
            return ResultadoCuota(false, "El socio no posee cuotas registradas", emptyList())
        }

        return ResultadoCuota(true, "Cuotas obtenidas con éxito", cuotas)
    }

    fun registrarCuotasSocio(socioId: Int, montoCuota: Double): Boolean {
        return cuotaRepo.registrarCuotasSocio(socioId, montoCuota)
    }
}
