package com.grupo3.clubdeportivo.clases.Modelos

class Socio : Cliente {
    var idSocio: Int = 0
    var estado: Boolean = false

    constructor(nombre: String, apellido: String, dni: String, aptoFisico: Boolean)
            : super(nombre, apellido, dni, aptoFisico)

    constructor(id: Int, nombre: String, apellido: String, dni: String, aptoFisico: Boolean)
            : super(id, nombre, apellido, dni, aptoFisico)

    constructor(id: Int, nombre: String, apellido: String, dni: String, aptoFisico: Boolean, estado: Boolean)
            : super(id, nombre, apellido, dni, aptoFisico) {
        this.estado = estado
    }

    fun estaActivo(): Boolean {
        return estado
    }
}
