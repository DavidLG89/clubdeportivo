package com.grupo3.clubdeportivo.ui.interfaces

interface ListenerVerificacion {
    val tituloRes: Int
    val opcionRes: Int
    val hintRes: Int
    val opcionesLista: Array<String>? get() = null
    val dniCliente: String? get() = null
    val nombreCliente: String? get() = null
    var seleccion: String?
    fun onVerficiacionVolver()
    fun onVerificacionContinuar()
}