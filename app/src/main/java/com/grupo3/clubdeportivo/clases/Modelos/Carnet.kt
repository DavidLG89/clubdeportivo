package com.grupo3.clubdeportivo.clases.Modelos

class Carnet {
    var id: Int = 0
    var inscripcion: Inscripcion? = null
    var estado: Boolean = false

    constructor(inscripcion: Inscripcion?, estado: Boolean) {
        this.inscripcion = inscripcion
        this.estado = estado
    }
}
