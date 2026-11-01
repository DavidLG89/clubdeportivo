package com.grupo3.clubdeportivo.clases.Modelos

import java.util.Date

class Pago {
    var id: Int = 0
    var fechaPago: Date = Date()
    var monto: Double = 0.0
    var metodoPago: MetodoPago? = null
    var cuota: Cuota? = null
    var noSocio: NoSocio? = null

    constructor()

    constructor(
        cuota: Cuota?,
        noSocio: NoSocio?,
        fechaPago: Date,
        monto: Double,
        metodoPago: MetodoPago?
    ) {
        this.cuota = cuota
        this.noSocio = noSocio
        this.fechaPago = fechaPago
        this.monto = monto
        this.metodoPago = metodoPago
    }
}
