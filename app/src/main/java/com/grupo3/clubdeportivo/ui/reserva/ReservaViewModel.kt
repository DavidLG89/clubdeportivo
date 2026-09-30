package com.grupo3.clubdeportivo.ui.reserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.data.model.EtapaReserva

class ReservaViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaReserva.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaReserva> = _pasoActual

    var dni: String = ""
    var actividad: String? = null
    var fechaHora: String? = null

    fun irAPaso(numero: EtapaReserva) {
        _pasoActual.value = numero
    }
}