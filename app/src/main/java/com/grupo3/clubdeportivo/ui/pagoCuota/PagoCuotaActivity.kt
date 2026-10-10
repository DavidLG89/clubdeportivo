package com.grupo3.clubdeportivo.ui.pagoCuota

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.componentes.CompBarraEstadoPago
import com.grupo3.clubdeportivo.ui.componentes.ListenerDni
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.fragmentos.pago.FragmVerificacionCuota

class PagoCuotaActivity : AppCompatActivity(), BarraEstadoHost<EtapaPago>, ListenerDni {

    private val viewModel: PagoCuotaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_cuota)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_cuota)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        stepBar = findViewById(R.id.componentStepBar)

        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
        }

        if (savedInstanceState == null) {
            irA(FragmBusquedaDni(), EtapaPago.BUSQUEDA_DNI)
        }
    }

    override fun setStep(etapa: EtapaPago) {
        stepBar.setStep(etapa)
    }

    override fun irA(fragment: Fragment, etapa: EtapaPago) {
        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        irA(FragmVerificacionCuota(), EtapaPago.VERIFICACION)
    }

    // Define color de barra de estado y card del dni
    override val colorBarraEstado = R.color.naranja
    override val colorCard = R.color.naranja
}
