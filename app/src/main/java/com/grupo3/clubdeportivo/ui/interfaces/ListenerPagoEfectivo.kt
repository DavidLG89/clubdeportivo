package com.grupo3.clubdeportivo.ui.interfaces

interface ListenerPagoEfectivo {
    var montoAPagar: Double
    fun registrarPago(efectivo: Double) : Boolean
    fun onPagoEfectivoVolver()
    fun onPagoEfectivoContinuar()
}