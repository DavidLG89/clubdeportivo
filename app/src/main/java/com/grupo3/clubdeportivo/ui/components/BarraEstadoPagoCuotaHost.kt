package com.grupo3.clubdeportivo.ui.components

import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.data.model.EtapaPagoCuota

interface BarraEstadoPagoCuotaHost {
    fun setStep(etapa: EtapaPagoCuota)
    fun avanzarA(fragment: Fragment, etapa: EtapaPagoCuota)
}
