package com.grupo3.clubdeportivo.ui.componentes

interface ListenerVerificacion {
    val tituloRes: Int
    val opcionRes: Int
    val hintRes: Int
    var seleccion: String?
    fun onVolver()
    fun onContinuar()
}