package com.grupo3.clubdeportivo.ui.componentes

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago

class CompBarraEstadoPago @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private lateinit var circles: List<TextView>

    init {
        inflate(context, R.layout.component_step_bar_pago_cuota, this)
        circles = listOf(
            findViewById(R.id.step1),
            findViewById(R.id.step2),
            findViewById(R.id.step3),
            findViewById(R.id.step4)
        )

        circles.forEach { it.setBackgroundResource(R.drawable.circle_grey) }
    }

    fun setStep(etapa: EtapaPago) {
        val pasoActual = etapa.numero
        val resActivo = (context as BarraEstadoHost<*>).colorBarraEstado ?: R.color.color_boton_principal2
        val colorActivo = ContextCompat.getColor(context, resActivo)
        circles.forEachIndexed { index, circle ->
            circle.backgroundTintList =
                if(index + 1 == pasoActual) ColorStateList.valueOf(colorActivo) else null

        }
    }
}
