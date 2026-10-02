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
import com.grupo3.clubdeportivo.data.model.EtapaPagoCuota
import com.grupo3.clubdeportivo.ui.components.BarraEstadoPagoCuotaHost
import com.grupo3.clubdeportivo.ui.components.CompBarraEstadoPagoCuota
import com.grupo3.clubdeportivo.ui.components.ListenerPagoCuota
import com.grupo3.clubdeportivo.ui.fragments.pagoCuota.FragmBusquedaDniCuota
import com.grupo3.clubdeportivo.ui.fragments.pagoCuota.FragmVerificacionCuota

class PagoCuotaActivity : AppCompatActivity(), BarraEstadoPagoCuotaHost, ListenerPagoCuota {

    private val viewModel: PagoCuotaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPagoCuota

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
            avanzarA(FragmBusquedaDniCuota(), EtapaPagoCuota.BUSQUEDA_DNI)
        }
    }

    override fun setStep(etapa: EtapaPagoCuota) {
        stepBar.setStep(etapa)
    }

    override fun avanzarA(fragment: Fragment, etapa: EtapaPagoCuota) {
        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        avanzarA(FragmVerificacionCuota(), EtapaPagoCuota.VERIFICACION)
    }
}
