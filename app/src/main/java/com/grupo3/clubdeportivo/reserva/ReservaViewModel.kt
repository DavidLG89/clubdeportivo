package com.grupo3.clubdeportivo.reserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservaViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(1)
    val pasoActual: LiveData<Int> = _pasoActual

    var dni: String = ""
    var actividad: String? = null
    var fechaHora: String? = null

    fun irAPaso(numero: Int) {
        _pasoActual.value = numero
    }
}