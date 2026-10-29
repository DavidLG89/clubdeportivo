package com.grupo3.clubdeportivo.clases.Modelos

class Socio : Cliente {
    var idSocio: Int = 0
    var estado: Boolean = false

    constructor(nombre: String, apellido: String, dni: String, email: String, aptoFisico: Boolean)
            : super(nombre, apellido, dni, email, aptoFisico)

    constructor(id: Int, nombre: String, apellido: String, dni: String, email: String, aptoFisico: Boolean)
            : super(id, nombre, apellido, dni, email, aptoFisico)

    constructor(id: Int, nombre: String, apellido: String, dni: String, email: String, aptoFisico: Boolean, estado: Boolean)
            : super(id, nombre, apellido, dni, email, aptoFisico) {
        this.estado = estado
    }

    fun estaActivo(): Boolean {
        return estado
    }
}
