package com.grupo3.clubdeportivo.ui.interfaces

interface ListenerVerificacion {
    val tituloRes: Int
    val opcionRes: Int
    val hintRes: Int
    var seleccion: String?
    fun onVerficiacionVolver()
    fun onVerificacionContinuar()
}