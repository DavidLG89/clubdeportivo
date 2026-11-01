package com.grupo3.clubdeportivo.data.model

data class DatosComprobante(
    val idPago: String = "0001",
    val dni: String? = null,
    val nombre: String? = null,
    val idReserva: String? = null,
    val actividad: String? = null,
    val monto: Int? = null,
    val metodoPago: String? = null,
    val cuota: Int? = null,
    val esNoSocio: Boolean = false,
    val fechaVencimiento: String? = null
)
