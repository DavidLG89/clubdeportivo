package com.grupo3.clubdeportivo.ui.interfaces

import com.grupo3.clubdeportivo.data.model.DatosComprobante

interface ListenerComprobantePago {
    fun datosComprobante(): DatosComprobante
    fun onComprobanteVolver()
    fun onComprobanteContinuar()
}
