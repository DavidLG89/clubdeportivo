package com.grupo3.clubdeportivo.ui.pagoCuota

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.DatosComprobante
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.componentes.CompBarraEstadoPago
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.MetodoPagoFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.PagoEnCuotasFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.VerificacionFragment
import com.grupo3.clubdeportivo.ui.fragmentos.pago.BancoFragment
import com.grupo3.clubdeportivo.ui.fragmentos.pago.ComprobantePagoFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerVerificacion

class PagoCuotaActivity : AppCompatActivity(),
    BarraEstadoHost<EtapaPago>,
    ListenerDni,
    ListenerVerificacion,
    ListenerMetodoPago,
    ListenerComprobantePago {

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

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {
                is BusquedaDniFragment -> viewModel.irAPaso(EtapaPago.BUSQUEDA_DNI)
                is VerificacionFragment -> viewModel.irAPaso(EtapaPago.VERIFICACION)
                is MetodoPagoFragment -> viewModel.irAPaso(EtapaPago.METODO_PAGO)
                is PagoEnCuotasFragment -> viewModel.irAPaso(EtapaPago.FORMALIZACION_PAGO)
                is ComprobantePagoFragment -> viewModel.irAPaso(EtapaPago.COMPROBANTE_PAGO)
            }
        }

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
        set(value) { viewModel.actividad = value }

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
        set(value) { viewModel.cuota = value }

    override fun onMetodoPagoVolver() {
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }

    override fun onMetodoPagoContinuar() {
        val metodo = viewModel.metodoPago
        if (metodo == "Tarjeta Crédito" || metodo == "Tarjeta de Crédito" || metodo == "Tarjeta Débito" || metodo == "Tarjeta de Débito") {
            irA(
                BancoFragment(),
                EtapaPago.FORMALIZACION_PAGO
            )
        } else if (metodo == "MercadoPago") {
            irA(
                PagoEnCuotasFragment(),
                EtapaPago.FORMALIZACION_PAGO
            )
        } else {
            irA(
                ComprobantePagoFragment(),
                EtapaPago.COMPROBANTE_PAGO
            )
        }
    }

    override fun datosComprobante(): DatosComprobante {
        val seleccionAnterior = viewModel.actividad ?: ""
        return DatosComprobante(
            idPago = "0001",
            dni = viewModel.dni,
            nombre = viewModel.nombre,
            idReserva = null,
            actividad = seleccionAnterior,
            monto = viewModel.monto,
            metodoPago = viewModel.metodoPago,
            cuota = viewModel.cuota,
            esNoSocio = false
        )
    }

    override fun onComprobanteVolver() {
        finish()
    }

    override fun onComprobanteContinuar() {
        val intent = Intent(this, MenuPrincipalActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}
