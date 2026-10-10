package com.grupo3.clubdeportivo.ui.componentes

import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.data.model.EtapaPago

interface BarraEstadoPagoCuotaHost {
    fun setStep(etapa: EtapaPago)
    fun avanzarA(fragment: Fragment, etapa: EtapaPago)
}
