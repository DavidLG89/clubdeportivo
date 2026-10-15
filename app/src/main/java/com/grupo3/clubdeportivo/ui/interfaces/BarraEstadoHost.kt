package com.grupo3.clubdeportivo.ui.interfaces

import androidx.fragment.app.Fragment

interface BarraEstadoHost<T> {
    fun setStep(etapa: T)
    fun irA(fragment: Fragment, etapa: T)
    val colorBarraEstado: Int
}