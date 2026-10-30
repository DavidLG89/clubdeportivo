package com.grupo3.clubdeportivo.ui.pagoCuota

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo
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
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerVerificacion
import java.text.SimpleDateFormat
import java.util.Locale

class PagoCuotaActivity : AppCompatActivity(),
    BarraEstadoHost<EtapaPago>,
    ListenerDni,
    ListenerVerificacion,
    ListenerMetodoPago,
    ListenerComprobantePago {

    private val viewModel: PagoCuotaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoPago
    val dbHelper = DbHelperObject.get(this)

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


        val cuotaRepo = CuotaRepo(dbHelper)
        val cuotasPendientes = cuotaRepo.obtenerCuotasRepo(dni).filter { it.estadoCuota == "Pendiente" }

        if (cuotasPendientes.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Socio / Cuota no encontrada")
                .setMessage("No se encontraron cuotas pendientes para el DNI $dni. Verifique que el cliente sea un socio registrado y posea cuotas pendientes.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Aceptar", null)
                .show()
        } else {
            val primeraCuota = cuotasPendientes[0]

            viewModel.dni = dni
            viewModel.listaCuotasPendientes = cuotasPendientes
            viewModel.cuotaPendiente = primeraCuota
            viewModel.idSocio = primeraCuota.socio?.idSocio
            viewModel.nombre = "${primeraCuota.socio?.nombre} ${primeraCuota.socio?.apellido}"
            viewModel.monto = primeraCuota.montoCuota.toInt()
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
                lista.map { "Cuota ID: ${it.idCuota}" }.toTypedArray()
            } else null
        }

    override var seleccion: String?
        get() = viewModel.cuotaPendiente?.let { "Cuota ID: ${it.idCuota}" }
        set(value) {
            val idExtraido = value?.replace("Cuota ID: ", "")?.trim()?.toIntOrNull()
            if (idExtraido != null) {
                val encontrada = viewModel.listaCuotasPendientes.find { it.idCuota == idExtraido }
                if (encontrada != null) {
                    viewModel.cuotaPendiente = encontrada
                    viewModel.monto = encontrada.montoCuota.toInt()
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

    fun procesarRegistroPagoBD(): Boolean {
        if (!viewModel.pagoRegistrado && viewModel.cuotaPendiente != null) {
            val pagosRepo = PagosRepo(dbHelper)
            val pagoCuotaServ = PagoCuotaServ(pagosRepo)

            val cuotaObj = viewModel.cuotaPendiente!!
            val montoAPagar = viewModel.monto?.toDouble() ?: cuotaObj.montoCuota

            val metodoStr = viewModel.metodoPago ?: "Efectivo"
            val idMetodo = when {
                metodoStr.contains("Mercado", ignoreCase = true) -> 4
                metodoStr.contains("Tarjeta", ignoreCase = true) -> 3
                metodoStr.contains("Cheque", ignoreCase = true) -> 2
                else -> 1 // Efectivo / Transferencia
            }
            val idConcepto = 2 // Concepto Cuota

            val resPago = pagoCuotaServ.registrarPago(cuotaObj, montoAPagar, idMetodo)
            if (resPago.ok && resPago.resultId > 0) {
                viewModel.pagoRegistrado = true
                viewModel.idPagoGenerado = String.format(Locale.getDefault(), "%04d", resPago.resultId)
                Toast.makeText(this, "Pago guardado con éxito en BD (ID: ${resPago.resultId})", Toast.LENGTH_SHORT).show()
                return true
            } else {
                Toast.makeText(this, "Aviso al registrar en BD: ${resPago.mensaje}", Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }

    override fun onMetodoPagoContinuar() {
        val metodo = viewModel.metodoPago ?: ""
        val esTarjeta = metodo.contains("Tarjeta", ignoreCase = true)
        val esMercadoPago = metodo.contains("Mercado", ignoreCase = true)

        if (esTarjeta) {
            irA(
                BancoFragment(),
                EtapaPago.FORMALIZACION_PAGO
            )
        } else if (esMercadoPago) {
            irA(
                PagoEnCuotasFragment(),
                EtapaPago.FORMALIZACION_PAGO
            )
        } else {
            // Efectivo, Transferencia u otros: registra RECIÉN el pago acá (NO ANTES)
            procesarRegistroPagoBD()
            irA(
                ComprobantePagoFragment(),
                EtapaPago.COMPROBANTE_PAGO
            )
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
            metodoPago = viewModel.metodoPago,
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
}
