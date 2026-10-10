package com.grupo3.clubdeportivo.ui.registroCliente

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.componentes.CompBarraRegistroCliente
import com.grupo3.clubdeportivo.ui.fragmentos.registroCliente.FragmRegistroClientePaso1
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity

class RegistroClienteActivity : AppCompatActivity(), BarraEstadoHost<EtapaRegistro> {

    private val viewModel: RegistroClienteViewModel by viewModels()
    private lateinit var stepBar: CompBarraRegistroCliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_cliente)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Barra de Estado
        stepBar = findViewById(R.id.componentStepBar)
        val tvTitle = findViewById<android.widget.TextView>(R.id.tvTitle)

        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
            tvTitle?.text = when (etapa) {
                EtapaRegistro.CUOTA_SOCIO -> "ANTECEDENTES CONTRATO"
                EtapaRegistro.CONTRATO -> "CONTRATO DE SOCIO"
                else -> "REGISTRO CLIENTE"
            }
        }

        if (savedInstanceState == null) {
            irA(FragmRegistroClientePaso1(), EtapaRegistro.REGISTRO_DATOS_CLIENTE)
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

    override fun setStep(etapa: EtapaRegistro) {
        stepBar.setStep(etapa)
    }

    override fun irA(fragment: Fragment, etapa: EtapaRegistro) {
        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
