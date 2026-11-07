package com.grupo3.clubdeportivo.ui.pagoCuota

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.clases.Modelos.NoSocio
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.clases.Servicios.CuotaServ
import com.grupo3.clubdeportivo.clases.Servicios.PagoCuotaServ
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
import com.grupo3.clubdeportivo.ui.fragmentos.pago.PagoEfectivoFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerPagoEfectivo
import com.grupo3.clubdeportivo.ui.interfaces.ListenerVerificacion
import java.text.SimpleDateFormat
import java.util.Locale

class PagoCuotaActivity : AppCompatActivity(),
    BarraEstadoHost<EtapaPago>,
    ListenerDni,
    ListenerVerificacion,
    ListenerMetodoPago,
    ListenerPagoEfectivo,
    ListenerComprobantePago {

    private val viewModel: PagoCuotaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago
    private val dbHelper by lazy { DbHelperObject.get(this) }
    private val pagoServ by lazy { PagoCuotaServ(PagosRepo(dbHelper)) }
    private val clienteServ by lazy { ClienteServ(ClienteRepo(dbHelper)) }
    private val cuotaServ by lazy { CuotaServ(CuotaRepo(dbHelper)) }


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

        val cuotasPendientes = cuotaServ.obtenerCuotasServ(dni)
        val cuota = cuotasPendientes.cuotas.filter { it.estadoCuota == "Pendiente" }

        val cliente = clienteServ.buscarClientePorDni(dni)
        android.util.Log.d("CUOTA", "cliente=$cliente clase=${cliente?.javaClass?.simpleName} dni=${cliente?.dni}")
        if(cliente == null) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente no encontrado"
            return
        }

        if(cliente is NoSocio) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente es no socio y no paga cuotas"
            return
        }
        if (cuota.isEmpty()) {
            val tvError = findViewById<TextView>(R.id.tvError)
            tvError.visibility = View.VISIBLE
            tvError.text = "Cliente no tiene cuotas pendientes"
            return
        } else {
            val primeraCuota = cuotasPendientes.cuotas[0]

            viewModel.dni = dni
            viewModel.listaCuotasPendientes = cuotasPendientes.cuotas
            viewModel.cuotaPendiente = primeraCuota
            viewModel.idSocio = primeraCuota.socio?.idSocio
            viewModel.nombre = "${primeraCuota.socio?.apellido}, ${primeraCuota.socio?.nombre}"
            viewModel.monto = primeraCuota.montoCuota
            viewModel.pagoRegistrado = false
            viewModel.idPagoGenerado = null

            irA(VerificacionFragment(), EtapaPago.VERIFICACION)
        }
    }

    // Define color de barra de estado y card del dni
    override val colorBarraEstado = R.color.naranja
    override val colorCard = R.color.naranja

    // Título y nombre dropdown de fragmento verificación
    override val tituloRes = R.string.verificacion_cuota_pendiente
    override val opcionRes = R.array.cuotas
    override val hintRes = R.string.cuota_nro

    override val dniCliente: String?
        get() = viewModel.dni

    override val nombreCliente: String?
        get() = viewModel.nombre

    override val opcionesLista: Array<String>?
        get() {
            val lista = viewModel.listaCuotasPendientes
            return if (lista.isNotEmpty()) {
                lista.map { cuota ->
                    val fechaFormatted = com.grupo3.clubdeportivo.clases.Helpers.FechaDb.formatearFecha(cuota.fechaVencimiento)
                    "Cuota ID: ${cuota.idCuota} - Venc: $fechaFormatted"
                }.toTypedArray()
            } else null
        }

    override var seleccion: String?
        get() = viewModel.cuotaPendiente?.let {
            val fechaFormatted = com.grupo3.clubdeportivo.clases.Helpers.FechaDb.formatearFecha(it.fechaVencimiento)
            "Cuota ID: ${it.idCuota} - Venc: $fechaFormatted"
        }
        set(value) {
            if (value != null) {
                val match = Regex("Cuota ID:\\s*(\\d+)").find(value)
                val idExtraido = match?.groupValues?.get(1)?.toIntOrNull()
                if (idExtraido != null) {
                    val encontrada = viewModel.listaCuotasPendientes.find { it.idCuota == idExtraido }
                    if (encontrada != null) {
                        viewModel.cuotaPendiente = encontrada
                        viewModel.monto = encontrada.montoCuota
                    }
                }
            }
        }

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

    override var seleccionMetodoPago: MetodoPago?
        get() = viewModel.metodoPago
        set(value) {
            viewModel.metodoPago = value }

    override var seleccionNumCuotas: Int?
        get() = viewModel.cuota
        set(value) { viewModel.cuota = value }

    override fun onMetodoPagoVolver() {
        irA(
            VerificacionFragment(),
            EtapaPago.VERIFICACION
        )
    }

    fun procesarRegistroPagoBD(): Boolean {
        if (!viewModel.pagoRegistrado && viewModel.cuotaPendiente != null) {

            val cuotaObj = viewModel.cuotaPendiente!!
            val montoAPagar = viewModel.monto?.toDouble() ?: cuotaObj.montoCuota

            val metodoPago = viewModel.metodoPago
            if (metodoPago != null) {
                Toast.makeText(this, "Requiere método de pago", Toast.LENGTH_SHORT).show()

                val resPago = pagoServ.registrarPago(cuotaObj, montoAPagar, metodoPago)

                if (resPago.ok && resPago.resultId > 0) {
                    viewModel.pagoRegistrado = true
                    viewModel.idPagoGenerado = String.format(Locale.getDefault(), "%04d", resPago.resultId)

                    return true
                } else {
                    Toast.makeText(
                        this,
                        "Aviso al registrar en BD: ${resPago.mensaje}",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
            }
            return true

    }

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
                irA(
                    ComprobantePagoFragment(),
                    EtapaPago.COMPROBANTE_PAGO
                )
            }
        }
    }

    override fun datosComprobante(): DatosComprobante {
        val idPago = viewModel.idPagoGenerado ?: "0001"
        val idCuotaStr = viewModel.cuotaPendiente?.idCuota?.toString() ?: ""
        val actividadDesc = if (idCuotaStr.isNotEmpty()) "Cuota ID: $idCuotaStr" else (viewModel.actividad ?: "")

        val fechaVencStr = viewModel.cuotaPendiente?.fechaVencimiento?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        } ?: ""

        return DatosComprobante(
            idPago = idPago,
            dni = viewModel.dni,
            nombre = viewModel.nombre,
            idReserva = null,
            actividad = actividadDesc,
            monto = viewModel.monto,
            metodoPago = viewModel.metodoPago?.nombre,
            cuota = viewModel.cuota,
            esNoSocio = false,
            fechaVencimiento = fechaVencStr
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
        val cuota = viewModel.cuotaPendiente
        val metodoPago = viewModel.metodoPago
        if(montoAPagar > 0.0) {
            val pago = pagoServ.registrarPago(cuota!!, montoAPagar, metodoPago!!)

            viewModel.idPagoGenerado = pago.resultId.toString()
        }
        return true
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
