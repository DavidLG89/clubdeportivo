package com.grupo3.clubdeportivo.ui.pagoReserva

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.componentes.ListenerDni
import com.grupo3.clubdeportivo.ui.componentes.CompBarraEstadoPago
import com.grupo3.clubdeportivo.ui.componentes.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.componentes.ListenerVerificacion
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.fragmentos.pago.ComprobantePagoFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.MetodoPagoFragment
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.VerificacionFragment


class PagoReservaActivity : AppCompatActivity(), BarraEstadoHost<EtapaPago>, ListenerDni, ListenerVerificacion, ListenerMetodoPago{

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

        // Establece el layout de la activity
        setContentView(R.layout.activity_pago_reserva)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_reserva)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Botón Volver regresa al fragmento anterior
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        // Obtiene la Barra de Estado del layout
        stepBar = findViewById(R.id.componentStepBar)

        // Reconoce el fragmento actual al volver con el botón del celular
        supportFragmentManager.addOnBackStackChangedListener {
            when(supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {
                is BusquedaDniFragment -> viewModel.irAPaso(EtapaPago.BUSQUEDA_DNI)
                is VerificacionFragment -> viewModel.irAPaso(EtapaPago.VERIFICACION)
                is MetodoPagoFragment -> viewModel.irAPaso(EtapaPago.METODO_PAGO)
                is ComprobantePagoFragment -> viewModel.irAPaso(EtapaPago.COMPROBANTE_PAGO)
            }
        }

        // Observa la etapa actual y actualiza la barra de estado
        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
        }

        // Carga el primer fragmento al iniciar la activity.
        // Además obtiene y guarda en variables los datos de otra activity
        if (savedInstanceState == null) {
            val dni = intent.getStringExtra(EXTRA_DNI)
            val nombre = intent.getStringExtra(EXTRA_NOMBRE)
            val actividad = intent.getStringExtra(EXTRA_ACTIVIDAD)
            if (intent.hasExtra(EXTRA_MONTO)) {
                viewModel.monto = intent.getIntExtra(EXTRA_MONTO, 0)
            }

            // Si recibe dni, guarda los datos en viewModel y continúa con la etapa método de pago
            if(!dni.isNullOrEmpty()) {
                viewModel.dni = dni
                viewModel.nombre = nombre
                viewModel.actividad = actividad

                irA(MetodoPagoFragment(), EtapaPago.METODO_PAGO)

            } else {
                irA(BusquedaDniFragment(), EtapaPago.BUSQUEDA_DNI)
            }
        }

        // Navegación en footer
        findViewById<LinearLayout>(R.id.navInicio)?.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.navPerfil)?.setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }
    }


    // Actualiza la barra de estado
    override fun setStep(etapa: EtapaPago) {
        stepBar.setStep(etapa)
    }


    // Navega en los distintos fragmentos de la activity
    override fun irA(fragment: Fragment, etapa: EtapaPago) {
        val esPrimero = supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null

        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .apply { if (!esPrimero) addToBackStack(null) }
            .commit()
    }

    // Si el dni está validado, continúa al paso siguiente y guarda el dato en viewModel
    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }

    // Define color de barra de estado y card del dni
    override val colorBarraEstado = R.color.color_boton_principal2
    override val colorCard = R.color.color_boton_principal2

    // Título y nombre dropdown de fragmento verificación
    override val tituloRes = R.string.verificacion_reserva
    override val opcionRes = R.array.actividades
    override val hintRes = R.string.actividad

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
        irA(
        ComprobantePagoFragment(),
        EtapaPago.COMPROBANTE_PAGO
        )
    }
}