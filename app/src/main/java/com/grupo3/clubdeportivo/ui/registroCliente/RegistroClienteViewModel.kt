package com.grupo3.clubdeportivo.ui.registroCliente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.data.model.EtapaRegistro

class RegistroClienteViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaRegistro.REGISTRO_DATOS_CLIENTE)
    val pasoActual: LiveData<EtapaRegistro> = _pasoActual

    var nombre: String = ""
    var apellido: String = ""
    var dni: String = ""
    var email: String = ""
    var esSocio: Boolean? = null
    var aptoFisico: Boolean = false
    var montoCuota: String = ""

    fun irAPaso(etapa: EtapaRegistro) {
        _pasoActual.value = etapa
    }
}
