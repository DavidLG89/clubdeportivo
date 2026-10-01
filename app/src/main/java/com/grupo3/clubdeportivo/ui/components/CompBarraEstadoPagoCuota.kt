package com.grupo3.clubdeportivo.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPagoCuota

class CompBarraEstadoPagoCuota @JvmOverloads constructor(
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
    }

    fun setStep(etapa: EtapaPagoCuota) {
        circles.forEachIndexed { index, circle ->
            val pasoActual = etapa.numero
            val numeroPaso = index + 1
            when {
                numeroPaso < pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_grey)
                }
                numeroPaso == pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_orange)
                }
                else -> {
                    circle.setBackgroundResource(R.drawable.circle_grey)
                }
            }
        }
    }
}
