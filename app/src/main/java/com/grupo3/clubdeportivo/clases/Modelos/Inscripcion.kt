package com.grupo3.clubdeportivo.clases.Modelos

import java.util.Date

class Inscripcion {
    var id: Int = 0
    var fechaInscripcion: Date = Date()
    var socio: Socio? = null

    constructor()

    constructor(id: Int, socio: Socio?) {
        this.id = id
        this.fechaInscripcion = Date()
        this.socio = socio
    }
}
