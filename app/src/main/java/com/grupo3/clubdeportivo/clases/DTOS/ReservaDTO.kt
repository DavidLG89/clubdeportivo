package com.grupo3.clubdeportivo.clases.DTOS

import java.util.Date

data class ReservaDTO(
    var idReserva: Int = 0,
    var idCliente: Int = 0,
    var nombreCliente: String = "",
    var apellidoCliente: String = "",
    var dni: String = "",
    var idActividad: Int = 0,
    var actividad: String = "",
    var precio: Double = 0.0,
    var fechaHora: Date = Date(),
    var estadoReserva: String = ""
)
