package com.grupo3.clubdeportivo.ui.reserva

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.components.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.components.CompBarraEstadoReserva
import com.grupo3.clubdeportivo.ui.fragments.FragmBusquedaDni



class Reserva : AppCompatActivity(), BarraEstadoHost {

    private val viewModel: ReservaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoReserva

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reserva)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reserva_actividades)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Botón volver
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Barra de Estado
        stepBar = findViewById(R.id.componentStepBar)


        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
        }
        if (savedInstanceState == null) {
            avanzarA(FragmBusquedaDni(), EtapaReserva.BUSQUEDA_DNI)
        }
    }


    // Etapa
    override fun setStep(etapa: EtapaReserva) {
        stepBar.setStep(etapa)
    }

    // Avanza etapa de formulario
    override fun avanzarA(fragment: Fragment, etapa: EtapaReserva) {
        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}