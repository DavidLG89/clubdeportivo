package com.grupo3.clubdeportivo.ui.pagoReserva

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.data.model.DatosComprobante
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
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

class PagoReservaActivity : AppCompatActivity(),
    BarraEstadoHost<EtapaPago>,
    ListenerDni,
    ListenerVerificacion,
    ListenerMetodoPago,
    ListenerComprobantePago {

    private val viewModel: PagoResViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago
    val dbHelper = DbHelperObject.get(this)

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
            when (supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {
                is BusquedaDniFragment -> viewModel.irAPaso(EtapaPago.BUSQUEDA_DNI)
                is VerificacionFragment -> viewModel.irAPaso(EtapaPago.VERIFICACION)
                is MetodoPagoFragment -> viewModel.irAPaso(EtapaPago.METODO_PAGO)
                is PagoEnCuotasFragment -> viewModel.irAPaso(EtapaPago.FORMALIZACION_PAGO)
                is ComprobantePagoFragment -> viewModel.irAPaso(EtapaPago.COMPROBANTE_PAGO)
            }
        }

        // Observa la etapa actual y actualiza la barra de estado
        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
        }

        // Carga el primer fragmento al iniciar la activity.
        if (savedInstanceState == null) {
            val dni = intent.getStringExtra(EXTRA_DNI)
            val nombre = intent.getStringExtra(EXTRA_NOMBRE)
            val actividad = intent.getStringExtra(EXTRA_ACTIVIDAD)
            if (intent.hasExtra(EXTRA_MONTO)) {
                viewModel.monto = intent.getIntExtra(EXTRA_MONTO, 0)
            }

            // Si recibe dni, guarda los datos en viewModel y continúa con la etapa método de pago
            if (!dni.isNullOrEmpty()) {
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
               val clienteRepo = com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo(dbHelper)
        val clienteServ = com.grupo3.clubdeportivo.clases.Servicios.ClienteServ(clienteRepo)
        val clienteFound = clienteServ.buscarClientePorDni(dni)

        viewModel.dni = dni
        viewModel.nombre = if (clienteFound != null) "${clienteFound.nombre} ${clienteFound.apellido}" else null
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

    override val dniCliente: String?
        get() = viewModel.dni

    override val nombreCliente: String?
        get() = viewModel.nombre

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
        val metodo = viewModel.metodoPago ?: ""
        when {
            metodo.contains("Tarjeta", ignoreCase = true) -> {
                irA(
                    BancoFragment(),
                    EtapaPago.FORMALIZACION_PAGO
                )
            }
            metodo.contains("Mercado", ignoreCase = true) -> {
                irA(
                    PagoEnCuotasFragment(),
                    EtapaPago.FORMALIZACION_PAGO
                )
            }
            else -> {
                irA(
                    ComprobantePagoFragment(),
                    EtapaPago.COMPROBANTE_PAGO
                )
            }
        }
    }

    override fun datosComprobante(): DatosComprobante {
        val seleccionAnterior = viewModel.actividad ?: ""
        return DatosComprobante(
            idPago = "0002",
            dni = viewModel.dni,
            nombre = viewModel.nombre,
            idReserva = null, // En no socio se ocultan los campos "Id reserva:" y su valor relacionado
            actividad = seleccionAnterior,
            monto = viewModel.monto,
            metodoPago = viewModel.metodoPago,
            cuota = viewModel.cuota,
            esNoSocio = true
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
