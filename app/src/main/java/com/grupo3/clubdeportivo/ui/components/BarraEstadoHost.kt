package com.grupo3.clubdeportivo.ui.components

import com.grupo3.clubdeportivo.data.model.EtapaReserva

interface BarraEstadoHost<T> {
    fun setStep(etapa: T)
    fun avanzarA(fragment: androidx.fragment.app.Fragment, etapa: T)
}