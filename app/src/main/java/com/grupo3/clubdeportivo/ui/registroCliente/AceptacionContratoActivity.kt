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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Cuota
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.InscripcionRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.clases.Servicios.InscripcionSocioServ
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity

class AceptacionContratoActivity : AppCompatActivity() {

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
            mensaje.setText(R.string.contrato_aceptado_por_el_cliente)

            val nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: ""
            val apellido = intent.getStringExtra("EXTRA_APELLIDO") ?: ""
            val dni = intent.getStringExtra("EXTRA_DNI") ?: ""
            val esSocio = intent.getBooleanExtra("EXTRA_ES_SOCIO", false)
            val aptoFisico = intent.getBooleanExtra("EXTRA_APTO_FISICO", false)
            val montoCuotaStr = intent.getStringExtra("EXTRA_MONTO_CUOTA") ?: "10000"

            Log.d("REGISTRO_CLIENTE", "AceptacionContrato: Nombre=$nombre, DNI=$dni, EsSocio=$esSocio")

            if (dni.isNotEmpty()) {
                val conexionBD = conexionBDsqlite()
                conexionBD.setContext(applicationContext)

                val clienteRepo = ClienteRepo(conexionBD)
                val clienteServ = ClienteServ(clienteRepo)

                // 1. Buscar si el cliente ya fue guardado en el paso de FormRegistroCliente
                var clienteTarget: Cliente? = clienteServ.buscarClientePorDni(dni)

                // 2. Si no fue guardado previamente, intentar registrarlo ahora
                if (clienteTarget == null && nombre.isNotEmpty()) {
                    val clienteNuevo = Cliente(0, nombre, apellido, dni, aptoFisico)
                    val resReg = clienteServ.realizarRegistro(clienteNuevo, esSocio)
                    if (resReg.ok) {
                        clienteTarget = resReg.cliente
                    }
                }

                // 3. Si es socio y tenemos el cliente guardado, formalizar su inscripción y cuota
                if (esSocio && clienteTarget != null) {
                    val inscripcionRepo = InscripcionRepo(conexionBD)
                    val inscripcionServ = InscripcionSocioServ(inscripcionRepo)
                    val montoCuota = montoCuotaStr.toDoubleOrNull() ?: 10000.0
                    val cuota = Cuota(montoCuota = montoCuota)

                    val resultadoInsc = inscripcionServ.formalizarSocio(clienteTarget, cuota)
                    Log.d("REGISTRO_CLIENTE", "Formalizar socio: ok=${resultadoInsc.ok}, msg=${resultadoInsc.mensaje}")

                    if (resultadoInsc.ok) {
                        Toast.makeText(this, "Socio e inscripción guardados con éxito en la base de datos", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Aviso inscripción: ${resultadoInsc.mensaje}", Toast.LENGTH_LONG).show()
                    }
                } else if (clienteTarget != null) {
                    Toast.makeText(this, "Cliente no socio guardado con éxito en la base de datos", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error al registrar cliente. Verifique si el DNI $dni ya existe.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Error: No se recibió DNI del cliente.", Toast.LENGTH_LONG).show()
            }
        }

        // Botón NO ACEPTA contrato
        btnNo.setOnClickListener {
            cardDecision.visibility = View.GONE
            linearMensaje.visibility = View.VISIBLE
            mensaje.setText(R.string.contrato_no_aceptado_por_el_cliente)
            iconoExito.visibility = View.GONE
            iconoRechazo.visibility = View.VISIBLE
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
