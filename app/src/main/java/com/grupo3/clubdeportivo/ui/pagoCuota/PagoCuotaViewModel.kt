package com.grupo3.clubdeportivo.ui.pagoCuota

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.data.model.EtapaPago

class PagoCuotaViewModel : ViewModel() {
    private val _pasoActual = MutableLiveData(EtapaPago.BUSQUEDA_DNI)
    val pasoActual: LiveData<EtapaPago> = _pasoActual

    var dni: String? = null
    var nombre: String? = null
    var idSocio: Int? = null
    var cuotaPendiente: CuotaDTO? = null
    var listaCuotasPendientes: List<CuotaDTO> = emptyList()
    var actividad: String? = null
    var monto: Int? = null
    var metodoPago: String? = null
    var cuota: Int? = null
    var pagoRegistrado: Boolean = false
    var idPagoGenerado: String? = null

    fun irAPaso(numero: EtapaPago) {
        _pasoActual.value = numero
    }
}
