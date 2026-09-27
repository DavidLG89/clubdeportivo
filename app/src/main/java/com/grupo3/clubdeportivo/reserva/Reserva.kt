package com.grupo3.clubdeportivo.reserva

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.components.StepBarView
import com.grupo3.clubdeportivo.fragments.FragmentDni

class Reserva : AppCompatActivity() {

    private val viewModel: ReservaViewModel by viewModels()
    private lateinit var stepBar: StepBarView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reserva_actividades)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val stepBar = findViewById<StepBarView>(R.id.componentStepBar)

        viewModel.pasoActual.observe(this) {
            paso -> stepBar.setStep(paso)
        }

        if(savedInstanceState == null) {
            avanzarA(FragmentDni(), 1)
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
    fun avanzarA(fragment: Fragment, numeroPaso: Int) {
        viewModel.irAPaso(numeroPaso)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}