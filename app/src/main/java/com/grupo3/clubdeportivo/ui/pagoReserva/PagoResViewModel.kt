package com.grupo3.clubdeportivo.ui.pagoReserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.data.model.EtapaPago

class PagoResViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaPago.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaPago> = _pasoActual

    var dni: String? = null
    var nombre: String? = ""
    var actividad: String? = null
    var monto: Double = 0.0
    var metodoPago: MetodoPago? = null
    var numCuotas: Int? = 0
    var reservas: List<ReservaDTO>? = emptyList()

    var reservaSeleccionada: ReservaDTO? = null
    var idPago: Int? = null
    fun irAPaso(numero: EtapaPago) {
        _pasoActual.value = numero
    }
}