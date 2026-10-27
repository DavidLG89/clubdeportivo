package com.grupo3.clubdeportivo.clases.Interfaces

data class ResultadoPago<T>(
    val ok: Boolean,
    val mensaje: String,
    val data: T?
)

interface IPago<T> {
    fun registrarPago(
        entidad: T,
        montoAPagar: Double,
        idConceptoPago: Int,
        idMedioPago: Int
    ): ResultadoPago<T>
}
