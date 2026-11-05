package com.grupo3.clubdeportivo.clases.Interfaces

import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

data class ResultadoPago<T>(
    val ok: Boolean,
    val mensaje: String,
    val data: T?,
    val resultId: Int = 0
)

interface IPago<T> {
    fun registrarPago(
        entidad: T,
        montoAPagar: Double,
        metodoPago: MetodoPago
    ): ResultadoPago<T>

}
