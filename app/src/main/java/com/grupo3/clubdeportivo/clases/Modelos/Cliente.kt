package com.grupo3.clubdeportivo.clases.Modelos

open class Cliente : Persona {
    var idCliente: Int = 0
    var aptoFisico: Boolean = false

    constructor() : super()

    constructor(nombre: String, apellido: String, dni: String, email:String, aptoFisico: Boolean) : super(nombre, apellido, dni, email) {
        this.aptoFisico = aptoFisico
    }

    constructor(id: Int, nombre: String, apellido: String, dni: String, email:String, aptoFisico: Boolean) : super(nombre, apellido, dni, email) {
        this.idCliente = id
        this.aptoFisico = aptoFisico
    }

    override fun toString(): String {
        return "$apellido, $nombre, $dni"
    }

    fun tieneAptoFisico(): Boolean {
        return aptoFisico
    }

    fun puedeReservar(): Boolean {
        return aptoFisico
    }
}
