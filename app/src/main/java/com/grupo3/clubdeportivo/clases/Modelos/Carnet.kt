package com.grupo3.clubdeportivo.clases.Modelos

class Carnet {
    var id: Int = 0
    var socio: Socio? = null
    var estado: Boolean = false

    constructor(socio: Socio?, estado: Boolean) {
        this.socio = socio
        this.estado = estado
    }
}
