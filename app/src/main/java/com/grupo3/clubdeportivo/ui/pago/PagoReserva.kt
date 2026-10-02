package com.grupo3.clubdeportivo.ui.pago

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.componentes.ListenerCompartido
import com.grupo3.clubdeportivo.ui.componentes.CompBarraEstadoPago
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva.FragmMetodoPago
import com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva.FragmVerificacionReserva


class PagoReserva : AppCompatActivity(), BarraEstadoHost<EtapaPago>, ListenerCompartido {

    private val viewModel: PagoResViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago

    companion object {
        const val EXTRA_DNI = "extra_dni"
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_ACTIVIDAD = "extra_actividad"
        const val EXTRA_MONTO = "extra_monto"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pago_reserva)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_reserva)) { v, insets ->
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
            val dni = intent.getStringExtra(EXTRA_DNI)
            val nombre = intent.getStringExtra(EXTRA_NOMBRE)
            val actividad = intent.getStringExtra(EXTRA_ACTIVIDAD)
            val monto = if (intent.hasExtra(EXTRA_MONTO)) intent.getIntExtra(EXTRA_MONTO, 0) else null
// ...
            viewModel.monto = monto

            if(!dni.isNullOrEmpty()) {
                viewModel.dni = dni
                viewModel.nombre = nombre
                viewModel.actividad = actividad
                viewModel.monto = monto?.toInt()

                avanzarA(FragmMetodoPago(), EtapaPago.METODO_PAGO)

            } else {
                avanzarA(FragmBusquedaDni(), EtapaPago.BUSQUEDA_DNI)
            }
        }
    }

    // Etapa
    override fun setStep(etapa: EtapaPago) {
        stepBar.setStep(etapa)
    }


    // Avanza etapa de formulario
    override fun avanzarA(fragment: Fragment, etapa: EtapaPago) {
        val esPrimero = supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null

        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .apply { if (!esPrimero) addToBackStack(null) }
            .commit()
    }

    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        avanzarA(
            FragmVerificacionReserva(),
            EtapaPago.VERIFICACION
        )
    }
}