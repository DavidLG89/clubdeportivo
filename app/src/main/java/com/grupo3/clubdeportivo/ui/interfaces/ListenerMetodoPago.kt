package com.grupo3.clubdeportivo.ui.interfaces

import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

interface ListenerMetodoPago {
    var seleccionMetodoPago: MetodoPago?
    var seleccionNumCuotas: Int?
    fun onMetodoPagoVolver()
    fun onMetodoPagoContinuar()
}