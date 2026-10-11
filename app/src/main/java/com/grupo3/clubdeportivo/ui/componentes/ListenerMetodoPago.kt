package com.grupo3.clubdeportivo.ui.componentes

interface ListenerMetodoPago {
    var seleccionMetodoPago: String?
    var seleccionNumCuotas: Int?
    fun onMetodoPagoVolver()
    fun onMetodoPagoContinuar()
}