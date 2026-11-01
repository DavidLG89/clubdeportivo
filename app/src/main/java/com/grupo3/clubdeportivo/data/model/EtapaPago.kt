package com.grupo3.clubdeportivo.data.model

enum class
EtapaPago(val numero: Int) {
    BUSQUEDA_DNI(1),
    VERIFICACION(2),
    METODO_PAGO(3),

    FORMALIZACION_PAGO(4),
    COMPROBANTE_PAGO(4)
}
