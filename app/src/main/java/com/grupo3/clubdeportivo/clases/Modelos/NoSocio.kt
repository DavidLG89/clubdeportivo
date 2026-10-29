package com.grupo3.clubdeportivo.clases.Modelos

class NoSocio : Cliente {
    var idNoSocio: Int = 0
    var accesoDiario: Boolean = false

    constructor(id: Int, nombre: String, apellido: String, dni: String, email: String, aptoFisico: Boolean)
            : super(id, nombre, apellido, dni, email, aptoFisico)

    fun debePagar(): Boolean {
        return true
    }
}
