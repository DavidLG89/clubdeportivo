package com.grupo3.clubdeportivo.clases.Interfaces

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
        idMedioPago: Int
    ): ResultadoPago<T>
}
