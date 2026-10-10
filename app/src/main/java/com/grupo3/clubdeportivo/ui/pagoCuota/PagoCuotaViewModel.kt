package com.grupo3.clubdeportivo.ui.pagoCuota

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.data.model.EtapaPago

class PagoCuotaViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaPago.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaPago> = _pasoActual

    var dni: String? = null
    var nombre: String? = "Sofía Egaña J."
    var actividad: String? = null
    var monto: Int? = 10000
    var metodoPago: String? = null
    var cuota: Int? = null

    fun irAPaso(numero: EtapaPago) {
        _pasoActual.value = numero
    }
}
