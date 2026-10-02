package com.grupo3.clubdeportivo.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro

class CompBarraRegistroCliente @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val circles: List<TextView>

    init {
        inflate(context, R.layout.component_step_bar_registro_cliente, this)
        circles = listOf(
            findViewById(R.id.step1),
            findViewById(R.id.step2),
            findViewById(R.id.step3)
        )
    }

    fun setStep(etapa: EtapaRegistro) {
        circles.forEachIndexed { index, circle ->
            val pasoActual = etapa.numero
            val numeroPaso = index + 1
            when {
                numeroPaso < pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_grey)
                }
                numeroPaso == pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_teal)
                }
                else -> {
                    circle.setBackgroundResource(R.drawable.circle_grey)
                }
            }
        }
    }
}
