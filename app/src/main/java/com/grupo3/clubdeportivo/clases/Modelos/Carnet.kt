package com.grupo3.clubdeportivo.clases.Modelos

class Carnet {
    var id: Int = 0
    var socio: Socio? = null
    var fechaEmision: String = ""
    var estado: Boolean = false

    constructor()

    constructor(id: Int, socio: Socio?, fechaEmision: String, estado: Boolean) {
        this.id = id
        this.socio = socio
        this.fechaEmision = fechaEmision
        this.estado = estado
    }

    constructor(socio: Socio?, estado: Boolean) {
        this.socio = socio
        this.estado = estado
    }
}
