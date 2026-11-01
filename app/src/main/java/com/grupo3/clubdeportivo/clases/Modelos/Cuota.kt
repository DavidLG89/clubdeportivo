package com.grupo3.clubdeportivo.clases.Modelos

import java.util.Date

class Cuota {
    var id: Int = 0
    var fechaVencimiento: Date = Date()
    var estadoPago: String = ""
    var socio: Socio? = null
    var montoCuota: Double = 0.0
    var pagos: MutableList<Pago> = mutableListOf()

    constructor()

    constructor(montoCuota: Double) {
        this.montoCuota = montoCuota
    }

    constructor(montoCuota: Double, fechaVencimiento: Date, estadoPago: String, socio: Socio?) {
        this.montoCuota = montoCuota
        this.fechaVencimiento = fechaVencimiento
        this.estadoPago = estadoPago
        this.socio = socio
    }
}
