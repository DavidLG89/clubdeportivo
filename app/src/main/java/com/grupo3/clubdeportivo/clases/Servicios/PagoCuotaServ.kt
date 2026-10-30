package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Interfaces.IPago
import com.grupo3.clubdeportivo.clases.Interfaces.ResultadoPago
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo

class PagoCuotaServ(private val repo: PagosRepo) : IPago<CuotaDTO> {

    override fun registrarPago(
        entidad: CuotaDTO,
        montoAPagar: Double,
        idConceptoPago: Int,
        idMedioPago: Int
    ): ResultadoPago<CuotaDTO> {
        if (montoAPagar < entidad.montoCuota) {
            return ResultadoPago(false, "Monto debe ser igual a la cuota", null)
        }

        if (montoAPagar <= 0) {
            return ResultadoPago(false, "El monto debe ser mayor a 0", null)
        }

        val result = repo.registrarPagoCuota(
            entidad.socio,
            montoAPagar,
            idMedioPago
        )

        if (result <= 0) {
            return ResultadoPago(false, "Error al realizar el registro de cuota", null)
        }

        return ResultadoPago(true, "Pago registrado con éxito", entidad, resultId = result)
    }
}
