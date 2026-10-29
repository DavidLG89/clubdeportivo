package com.grupo3.clubdeportivo.clases.Modelos

import java.util.Date

class Reserva {
    var id: Int = 0
    var programacion: Programacion? = null
    var cliente: Cliente? = null
    var fechaHoraReserva: Date = Date()
    var estado: String = ""
}
