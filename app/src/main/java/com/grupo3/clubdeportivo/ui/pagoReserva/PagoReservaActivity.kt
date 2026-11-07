package com.grupo3.clubdeportivo.ui.pagoReserva

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.clases.Modelos.NoSocio
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ReservaRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.clases.Servicios.PagoReservaServ
import com.grupo3.clubdeportivo.clases.Servicios.ReservaServ
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
import com.grupo3.clubdeportivo.ui.fragmentos.pago.PagoEfectivoFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerPagoEfectivo
import com.grupo3.clubdeportivo.ui.interfaces.ListenerVerificacion

class PagoReservaActivity : AppCompatActivity(),
    BarraEstadoHost<EtapaPago>,
    ListenerDni,
    ListenerVerificacion,
    ListenerMetodoPago,
    ListenerComprobantePago,
    ListenerPagoEfectivo {
    companion object {
        const val EXTRA_DNI = "extra_dni"
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_ACTIVIDAD = "extra_actividad"
        const val EXTRA_MONTO = "extra_monto"
        const val EXTRA_ID_RESERVA = "extra_id_reserva"
    }

    // Dependencias
    private val viewModel: PagoResViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago
    private val dbHelper by lazy { DbHelperObject.get(this) }
    private val clienteServ by lazy { ClienteServ(ClienteRepo(dbHelper)) }
    private val reservaServ by lazy { ReservaServ(ReservaRepo(dbHelper)) }
    private val pagoServ by lazy { PagoReservaServ(PagosRepo(dbHelper)) }

    var cliente = Cliente()


    // Ciclo de vida: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Establece el layout de la activity
        setContentView(R.layout.activity_pago_reserva)
        configurarInsets()
        configurarBarraEstado()
        configurarFooter()

        if (savedInstanceState == null) iniciarFlujo()
    }

    private fun configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_reserva)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Obtiene la Barra de Estado del layout / Botón Volver regresa al fragmento anterior
    private fun configurarBarraEstado() {
        stepBar = findViewById(R.id.componentStepBar)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        // Reconoce el fragmento actual al volver con el botón del celular
        supportFragmentManager.addOnBackStackChangedListener {
            establecerEtapa(supportFragmentManager.findFragmentById(R.id.fragmentContainer))
                ?.let { viewModel.irAPaso(it) }
        }
        // Observa la etapa actual y actualiza la barra de estado
        viewModel.pasoActual.observe(this) {
            setStep(it)
        }
    }

    // Si llega con DNI (desde otra pantalla), salta directo al méthodo de pago
    private fun iniciarFlujo() {

        val dni = viewModel.dni ?: intent.getStringExtra(EXTRA_DNI)
        val nombre = viewModel.nombreCompleto ?: intent.getStringExtra(EXTRA_NOMBRE)
        val actividad = viewModel.actividad ?: intent.getStringExtra(EXTRA_ACTIVIDAD)



        if (intent.hasExtra(EXTRA_MONTO)) {
            viewModel.monto = intent.getDoubleExtra(EXTRA_MONTO, 0.0)
        }

        if(intent.hasExtra(EXTRA_ID_RESERVA)) {
            viewModel.idReserva = intent.getIntExtra(EXTRA_ID_RESERVA, -1)
        }

        val idReserva = viewModel.idReserva

        if(idReserva != null && idReserva > 0) {
            viewModel.reservaSeleccionada = reservaServ.buscarReservaPorId(idReserva)
        }

        if (dni.isNullOrEmpty()) {

            irA(BusquedaDniFragment(), EtapaPago.BUSQUEDA_DNI)
        } else {
            viewModel.dni = dni
            viewModel.nombreCompleto = nombre
            viewModel.actividad = actividad

            irA(MetodoPagoFragment(), EtapaPago.METODO_PAGO)
        }
    }

    // Navegación en footer
    private fun configurarFooter() {
        findViewById<LinearLayout>(R.id.navInicio)?.setOnClickListener {
            irAMenuPrincipal()
        }

        findViewById<LinearLayout>(R.id.navPerfil)?.setOnClickListener {
            startActivity(Intent(this, PerfilAdminActivity::class.java))
        }
    }

    private fun irAMenuPrincipal() {
        val intent = Intent(this, MenuPrincipalActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun establecerEtapa(fragment: Fragment?): EtapaPago? = when (fragment) {
        is BusquedaDniFragment -> EtapaPago.BUSQUEDA_DNI
        is VerificacionFragment -> EtapaPago.VERIFICACION
        is MetodoPagoFragment -> EtapaPago.METODO_PAGO
        is PagoEnCuotasFragment -> EtapaPago.FORMALIZACION_PAGO
        is ComprobantePagoFragment -> EtapaPago.COMPROBANTE_PAGO
        else -> null
    }

    // NAVEGACIÓN
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

    // PASO BÚSQUEDA DNI (ListenerDni)
// Define color de barra de estado y card del dni
    override val colorBarraEstado = R.color.color_boton_principal2
    override val colorCard = R.color.color_boton_principal2

    // Si el dni está validado, continúa al paso siguiente y guarda el dato en viewModel
    override fun onDniValidado(dni: String) {

        val cliente = clienteServ.buscarClientePorDni(dni)

        if(cliente == null) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente no encontrado"
            return
        }
        if(cliente !is NoSocio) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente es socio y no paga reservas"
            return
        }

        val reservas = reservaServ.buscarReservasPorDni(dni)

        if (reservas.isNullOrEmpty()) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente no tiene reservas"
            return
        }

        viewModel.dni = cliente?.dni
        viewModel.nombreCompleto =
            cliente?.mostrarNombreCompleto()
        viewModel.reservas = reservas
        viewModel.reservaSeleccionada = null
        viewModel.actividad = null
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }


    // PASO VERIFICACIÓN RESERVA (ListenerVerificacion)
    override val tituloRes = R.string.verificacion_reserva
    override val opcionRes = R.array.actividades // solo a modo de respaldo
    override val hintRes = R.string.actividad

    override val dniCliente: String? get() = viewModel.dni
    override val nombreCliente: String? get() = viewModel.nombreCompleto
    override val opcionesLista: Array<String>?
        get() = viewModel.reservas?.map { textoReserva(it) }?.toTypedArray()

    override var seleccion: String?
        get() = viewModel.actividad
        set(value) {
            viewModel.actividad = value
            viewModel.reservaSeleccionada =
                viewModel.reservas?.firstOrNull { textoReserva(it) == value }
        }

    private fun textoReserva(r: ReservaDTO): String {
        val fecha = FechaDb.formatearFecha(r.fechaHoraReserva)
        val sufijo = if (r.cuposDisponibles <= 0) " (sin cupo)" else ""
        return "${r.actividad} ~ $fecha$sufijo"
    }

    override fun onVerficiacionVolver() {
        irA(
            BusquedaDniFragment(),
            EtapaPago.BUSQUEDA_DNI
        )
    }

    override fun onVerificacionContinuar() {
        val reserva = viewModel.reservaSeleccionada ?: return

        if (reserva.cuposDisponibles <= 0) {
            Toast.makeText(
                this,
                "Reserva registrada pero sin cupos disponibles",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.monto = reserva.precio
        irA(
            MetodoPagoFragment(),
            EtapaPago.METODO_PAGO
        )
    }

    // PASO MÉTHODO DE PAGO (ListenerMetodoPago)

    override var seleccionMetodoPago: MetodoPago?
        get() = viewModel.metodoPago
        set(value) {
            viewModel.metodoPago = value }


    override var seleccionNumCuotas: Int?
        get() = viewModel.numCuotas
        set(value) {
            viewModel.numCuotas = value
        }

    override fun onMetodoPagoVolver() {
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMetodoPagoContinuar() {
        val nombreMetodo = viewModel.metodoPago?.nombre ?: return
            when {
                nombreMetodo.contains("Tarjeta", ignoreCase = true) -> {
                    irA(
                        BancoFragment(),
                        EtapaPago.FORMALIZACION_PAGO
                    )
                }

                nombreMetodo.contains("Mercado", ignoreCase = true) -> {
                    irA(
                        PagoEnCuotasFragment(),
                        EtapaPago.FORMALIZACION_PAGO
                    )
                }

                nombreMetodo.contains("Efectivo", ignoreCase = true) -> {
                    irA(
                        PagoEfectivoFragment(),
                        EtapaPago.FORMALIZACION_PAGO
                    )
                }

                else -> {
                    Toast.makeText(this, "No hay método de pago seleccionado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Paso Comprobante de Pago (ListenerComprobantePago)
    override fun datosComprobante(): DatosComprobante {
        val reserva = viewModel.reservaSeleccionada

        return DatosComprobante(
            idPago = viewModel.idPago.toString().padStart(4, '0'),
            dni = reserva?.dni ?: viewModel.dni,
            nombre = reserva?.nombreCliente ?: viewModel.nombreCompleto,
            idReserva = (reserva?.idReserva ?: viewModel.idReserva).toString(),
            actividad = reserva?.actividad ?: viewModel.actividad,
            monto = reserva?.precio ?: viewModel.monto,
            metodoPago = viewModel.metodoPago?.nombre,
            cuota = null, // No es cuota (no socio)
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

    override var montoAPagar: Double
        get() = viewModel.monto
        set(value) {
            viewModel.monto = value
        }

    override fun registrarPago(efectivo: Double): Boolean {
        val reserva = viewModel.reservaSeleccionada ?: return false
        val metodoPago = viewModel.metodoPago ?: return false

        if (montoAPagar <= 0.0) return false

        val pago = pagoServ.registrarPago(reserva, montoAPagar, metodoPago)

        if (pago.ok) {
            viewModel.idPago = pago.resultId
            return true
        }
        return false
    }

    override fun onPagoEfectivoVolver() {
        irA(
            MetodoPagoFragment(),
            EtapaPago.METODO_PAGO
        )
    }
    override fun onPagoEfectivoContinuar() {
        irA(
            ComprobantePagoFragment(),
            EtapaPago.COMPROBANTE_PAGO
        )
    }
}
