package com.grupo3.clubdeportivo.clases.Modelos

open class Persona {
    var id: Int = 0
    var nombre: String = ""
    var apellido: String = ""
    var dni: String = ""

    constructor()

    constructor(nombre: String, apellido: String, dni: String) {
        this.nombre = nombre
        this.apellido = apellido
        this.dni = dni
    }

    fun mostrarNombreCompleto(): String {
        return "$nombre $apellido"
    }
}
