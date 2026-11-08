package com.grupo3.clubdeportivo.ui.vencimiento

import androidx.lifecycle.ViewModel

/**
 * ViewModel compartido que mantiene el estado de búsqueda por DNI y el historial de cuotas del socio.
 *
 * Preserva la información entre la actividad de búsqueda [BuscarVencimientoPorDniActivity]
 * y el fragmento de resultados [CuotasSocioFragment].
 */
class VencimientoDniViewModel : ViewModel() {
    /** Número de DNI del socio buscado. */
    var dni: String = ""

    /** Nombre y apellido completo del socio. */
    var nombreSocio: String = ""

    /** Número de identificación de socio. */
    var numeroSocio: String = ""

    /** Lista de filas formateadas de cuotas (Id, Vencimiento, Monto, Estado). */
    var cuotasSocio: List<List<String>> = emptyList()
}
