package com.grupo3.clubdeportivo.ui.vencimiento

import androidx.lifecycle.ViewModel

class VencimientoDniViewModel : ViewModel() {
    var dni: String = ""
    var nombreSocio: String = ""
    var numeroSocio: String = ""
    var cuotasSocio: List<List<String>> = emptyList()
}
