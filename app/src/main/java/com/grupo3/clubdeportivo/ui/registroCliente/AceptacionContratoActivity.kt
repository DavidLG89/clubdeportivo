package com.grupo3.clubdeportivo.ui.registroCliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.clases.Servicios.CuotaServ
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity

class AceptacionContratoActivity : AppCompatActivity() {
    private val dbHelper  by lazy { DbHelperObject.get(this) }
    private val clienteServ by lazy { ClienteServ(ClienteRepo(dbHelper)) }
    private val cuotaServ by lazy { CuotaServ(CuotaRepo(dbHelper), ClienteRepo(dbHelper)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aceptacion_contrato)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón volver atrás
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnSi = findViewById<Button>(R.id.btnSi)
        val btnNo = findViewById<Button>(R.id.btnNo)
        val mensaje = findViewById<TextView>(R.id.tvMensajeSi)
        val cardDecision = findViewById<LinearLayout>(R.id.cardDecisionContrato)
        val linearMensaje = findViewById<LinearLayout>(R.id.linearMensaje)
        val iconoExito = findViewById<ImageView>(R.id.ivIconoExito)
        val iconoRechazo = findViewById<ImageView>(R.id.ivIconoRechazo)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Botón ACEPTA contrato
        btnSi.setOnClickListener {
            cardDecision.visibility = View.GONE
            linearMensaje.visibility = View.VISIBLE
            iconoExito.visibility = View.VISIBLE
            iconoRechazo.visibility = View.GONE

            val nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: ""
            val apellido = intent.getStringExtra("EXTRA_APELLIDO") ?: ""
            val dni = intent.getStringExtra("EXTRA_DNI") ?: ""
            val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
            val aptoFisico = intent.getBooleanExtra("EXTRA_APTO_FISICO", false)
            val montoCuotaStr = intent.getStringExtra("EXTRA_MONTO_CUOTA") ?: "10000"

            Log.d("REGISTRO_CLIENTE", "AceptacionContrato Acepta: Nombre=$nombre, DNI=$dni")

            if (dni.isNotEmpty()) {

                var clienteTarget: Cliente? = clienteServ.buscarClientePorDni(dni)

                if (clienteTarget == null && nombre.isNotEmpty()) {
                    val clienteNuevo = Cliente(0, nombre, apellido, dni, email, aptoFisico)
                    val resReg = clienteServ.realizarRegistro(clienteNuevo, true)
                    if (resReg.ok) {
                        clienteTarget = resReg.cliente
                    }
                }

                if (clienteTarget != null) {
                    val idSocio = clienteServ.registrarSocio(clienteTarget.idCliente)

                    if (idSocio > 0) {
                        val montoCuota = montoCuotaStr.toDoubleOrNull() ?: 10000.0
                        cuotaServ.registrarCuotasSocio(idSocio, montoCuota)
                        mensaje.text = "Contrato aceptado por el cliente.\n\nSe registró como socio."
                    } else {
                        mensaje.text = "Ocurrió un error al registrar la condición de socio."
                    }
                } else {
                    mensaje.text = "Error al registrar cliente. Verifique si el DNI $dni ya existe."
                }
            } else {
                mensaje.text = "Error: No se recibió DNI del cliente."
            }
        }

        // Botón NO ACEPTA contrato
        btnNo.setOnClickListener {
            cardDecision.visibility = View.GONE
            linearMensaje.visibility = View.VISIBLE
            iconoExito.visibility = View.GONE
            iconoRechazo.visibility = View.VISIBLE

            val nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: ""
            val apellido = intent.getStringExtra("EXTRA_APELLIDO") ?: ""
            val dni = intent.getStringExtra("EXTRA_DNI") ?: ""
            val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
            val aptoFisico = intent.getBooleanExtra("EXTRA_APTO_FISICO", false)

            Log.d("REGISTRO_CLIENTE", "AceptacionContrato Rechaza: Nombre=$nombre, DNI=$dni")

            if (dni.isNotEmpty()) {
                val clienteRepo = ClienteRepo(dbHelper)
                val clienteServ = ClienteServ(clienteRepo)

                var clienteTarget: Cliente? = clienteServ.buscarClientePorDni(dni)

                if (clienteTarget == null && nombre.isNotEmpty()) {
                    val clienteNuevo = Cliente(0, nombre, apellido, dni, email, aptoFisico)
                    val resReg = clienteServ.realizarRegistro(clienteNuevo, false)
                    if (resReg.ok) {
                        clienteTarget = resReg.cliente
                    }
                }

                if (clienteTarget != null) {
                    clienteServ.registrarNoSocio(clienteTarget.idCliente)
                    mensaje.text = "Contrato NO ha sido aceptado por el cliente.\n\nSe registró como no socio."
                } else {
                    mensaje.text = "Error al registrar cliente. Verifique si el DNI $dni ya existe."
                }
            } else {
                mensaje.text = "Error: No se recibió DNI del cliente."
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
        }

        // Navegación en footer
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }
    }
}
