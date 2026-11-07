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

class CuotaServ(private val cuotaRepo: CuotaRepo, private val clienteRepo : ClienteRepo) {

    fun obtenerCuotasSocioServ(dni: String): ResultadoCuota {
        val cliente = clienteRepo.buscarClientePorDniRepo(dni)

        if(cliente !is Socio) {
            return ResultadoCuota(false, "DNI no corresponde a un socio", emptyList())
        }
        val cuotasPendientes = cuotaRepo.obtenerCuotasRepo(dni).filter { it.estadoCuota == "Pendiente" }

        if(cuotasPendientes.isEmpty()) {
            return ResultadoCuota(false, "Socio no tiene cuotas pendientes", emptyList())
        }
        return ResultadoCuota(true, "Cuota pendiente obtenida con éxito", cuotasPendientes)
    }

    fun registrarCuotasSocio(socioId: Int, montoCuota: Double): Boolean {
        return cuotaRepo.registrarCuotasSocio(socioId, montoCuota)
    }
}
