package com.grupo3.clubdeportivo.ui.componentes

import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.data.model.EtapaRegistro

interface BarraEstadoRegistroHost {
    fun setStep(etapa: EtapaRegistro)
    fun avanzarA(fragment: Fragment, etapa: EtapaRegistro)
}
