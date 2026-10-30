package com.grupo3.clubdeportivo.clases.Modelos

class Usuario : Persona {
    var username: String = ""
    var password: String = ""
    var rol: Rol? = null
    var activo: Boolean = false

    constructor() : super()

    constructor(nombre: String, apellido: String, dni: String, email: String) : super(nombre, apellido, dni, email)

    constructor(username: String, rol: Rol) {
        this.username = username
        this.rol = rol
    }
}
