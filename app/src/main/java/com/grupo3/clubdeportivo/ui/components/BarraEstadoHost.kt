package com.grupo3.clubdeportivo.ui.components

import com.grupo3.clubdeportivo.data.model.EtapaReserva

interface BarraEstadoHost {
    fun setStep(etapa: EtapaReserva)
    fun avanzarA(fragment: androidx.fragment.app.Fragment, etapa: EtapaReserva)
}