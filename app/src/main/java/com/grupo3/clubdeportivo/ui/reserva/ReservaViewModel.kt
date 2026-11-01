package com.grupo3.clubdeportivo.ui.reserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.clases.Modelos.Reserva
import com.grupo3.clubdeportivo.data.model.EtapaReserva

class ReservaViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaReserva.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaReserva> = _pasoActual

    var cliente = Cliente()
    var tipoCliente: String = ""
    var actividad = Actividad()
    var fecha: String = ""
    var monto: Int = 0

    var programacion: Programacion? = null
    var reserva: ReservaDTO? = null
    val nombreCompleto: String
       get() = "${cliente.nombre} ${cliente.apellido}"

    val nombreCompletoReserva: String
        get() = reserva?.let { "${it.nombreCliente} ${it.apellidoCliente}" } ?: ""

    fun irAPaso(numero: EtapaReserva) {

        _pasoActual.value = numero
    }
}