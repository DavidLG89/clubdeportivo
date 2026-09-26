package com.grupo3.clubdeportivo.components


import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.R
import androidx.core.content.ContextCompat
import com.grupo3.clubdeportivo.components.R

class StepBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private lateinit var circles: List<TextView>

    init {
        inflate(context, R.layout, this)
        circles = listOf(
            findViewById(R.id.step1),
            findViewById(R.id.step2),
            findViewById(R.id.step3),
            findViewById(R.id.step4)
        )
    }

    fun setStep(pasoActual: Int) {
        circles.forEachIndexed { index, circle ->
            val numeroPaso = index + 1
            when {
                numeroPaso < pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_completed)
                    circle.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                numeroPaso == pasoActual -> {
                    circle.setBackgroundResource(R.drawable.circle_teal)
                    circle.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                else -> {
                    circle.setBackgroundResource(R.drawable.circle_grey)
                    circle.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
        }
    }
}