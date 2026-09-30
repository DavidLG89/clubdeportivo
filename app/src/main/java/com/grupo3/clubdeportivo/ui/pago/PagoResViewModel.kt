package com.grupo3.clubdeportivo.ui.pago

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.data.model.EtapaReserva

class PagoResViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaPago.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaPago> = _pasoActual

    var dni: String = ""
    var nombre: String = ""
    var actividad: String? = null
    var monto: Int? = null
    var pago: String? = null
    var cuota: Int? = null

    fun irAPaso(numero: EtapaPago) {
        _pasoActual.value = numero
    }
}