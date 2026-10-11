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
import com.grupo3.clubdeportivo.ui.componentes.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.componentes.ListenerVerificacion
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.VerificacionFragment
import com.grupo3.clubdeportivo.ui.fragmentos.pago.ComprobantePagoFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.MetodoPagoFragment

class PagoCuotaActivity : AppCompatActivity(), BarraEstadoHost<EtapaPago>, ListenerDni, ListenerVerificacion, ListenerMetodoPago {

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
            irA(BusquedaDniFragment(), EtapaPago.BUSQUEDA_DNI)
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
        irA(VerificacionFragment(), EtapaPago.VERIFICACION)
    }

    // Define color de barra de estado y card del dni
    override val colorBarraEstado = R.color.naranja
    override val colorCard = R.color.naranja

    // Título y nombre dropdown de fragmento verificación
    override val tituloRes = R.string.verificacion_cuota_pendiente
    override val opcionRes = R.array.cuotas
    override val hintRes = R.string.cuota_nro

    override var seleccion: String?
        get() = viewModel.actividad
        set(value) { viewModel.actividad = value}

    override fun onVerficiacionVolver() {
        irA(
            BusquedaDniFragment(),
            EtapaPago.BUSQUEDA_DNI
        )
    }

    override fun onVerificacionContinuar() {
        irA(
            MetodoPagoFragment(),
            EtapaPago.METODO_PAGO
        )
    }

    override var seleccionMetodoPago: String?
        get() = viewModel.metodoPago
        set(value) { viewModel.metodoPago = value }

    override var seleccionNumCuotas: Int?
        get() = viewModel.cuota
        set(value) { viewModel.cuota = value}

    override fun onMetodoPagoVolver() {
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }

    override fun onMetodoPagoContinuar() {
        irA(
            ComprobantePagoFragment(),
            EtapaPago.COMPROBANTE_PAGO
        )
    }
}
