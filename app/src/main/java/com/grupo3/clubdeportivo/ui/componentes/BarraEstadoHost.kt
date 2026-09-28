package com.grupo3.clubdeportivo.ui.componentes

interface BarraEstadoHost<T> {
    fun setStep(etapa: T)
    fun avanzarA(fragment: androidx.fragment.app.Fragment, etapa: T)
}