package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Interfaces.IPago
import com.grupo3.clubdeportivo.clases.Interfaces.ResultadoPago
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo

class PagoActividad(private val repo: PagosRepo) : IPago<ReservaDTO> {

    override fun registrarPago(
        entidad: ReservaDTO,
        montoAPagar: Double,
        idMedioPago: Int
    ): ResultadoPago<ReservaDTO> {
        if (entidad.estadoReserva != "Pendiente de pago") {
            return ResultadoPago(false, "No existe reserva pendiente de pago", null)
        }

        if (montoAPagar <= 0) {
            return ResultadoPago(false, "El monto debe ser mayor a 0", null)
        }

        if (montoAPagar < entidad.precio) {
            return ResultadoPago(false, "Monto a pagar no coincide con el monto de la actividad", null)
        }

        val result = repo.registrarPagoActividad(
            entidad.idReserva,
            montoAPagar,
            idMedioPago
        )

        if (result <= 0) {
            return ResultadoPago(false, "No se pudo procesar el pago", null)
        }

        entidad.estadoReserva = "Pagada"

        return ResultadoPago(true, "Pago realizado con éxito", entidad, resultId = result)
    }
}
