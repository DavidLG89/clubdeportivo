package com.grupo3.clubdeportivo.ui.pagoReserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.data.model.EtapaPago

class PagoResViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaPago.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaPago> = _pasoActual

    var dni: String? = null
    var nombre: String? = "Sofía Egaña J." // harcodeado
    var actividad: String? = null
    var monto: Int? = 10000 // harcodeado
    var metodoPago: String? = null
    var cuota: Int? = null

    fun irAPaso(numero: EtapaPago) {
        _pasoActual.value = numero
    }
}