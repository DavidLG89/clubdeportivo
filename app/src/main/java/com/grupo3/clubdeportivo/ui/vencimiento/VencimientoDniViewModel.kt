package com.grupo3.clubdeportivo.ui.vencimiento

import androidx.lifecycle.ViewModel

class VencimientoDniViewModel : ViewModel() {
    var dni: String = ""
    var nombreSocio: String = "Juan Perez"
    var numeroSocio: String = "1403"

    val cuotasSocio: List<List<String>> = listOf(
        listOf("1", "01/01/2025", "$30.000", "Pagado"),
        listOf("2", "01/02/2025", "$30.000", "Pagado"),
        listOf("3", "01/03/2025", "$30.000", "Vencida"),
        listOf("4", "01/04/2025", "$30.000", "Pendiente")
    )
}
