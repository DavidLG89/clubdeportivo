package com.grupo3.clubdeportivo.clases.Modelos

class NoSocio : Cliente {
    var idNoSocio: Int = 0
    var accesoDiario: Boolean = false

    constructor(nombre: String, apellido: String, dni: String, aptoFisico: Boolean)
            : super(nombre, apellido, dni, aptoFisico)

    constructor(id: Int, nombre: String, apellido: String, dni: String, aptoFisico: Boolean)
            : super(id, nombre, apellido, dni, aptoFisico)

    fun debePagar(): Boolean {
        return true
    }
}
