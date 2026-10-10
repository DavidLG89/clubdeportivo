package com.grupo3.clubdeportivo.ui.componentes

import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.data.model.EtapaRegistro

interface BarraEstadoHost<T> {
    fun setStep(etapa: T)
    fun irA(fragment: Fragment, etapa: T)
}
