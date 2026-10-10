package com.grupo3.clubdeportivo.ui.vencimiento

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import com.grupo3.clubdeportivo.ui.componentes.ListenerDni
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni

class BuscarVencimientoPorDniActivity : AppCompatActivity(), ListenerDni {

    private val viewModel: VencimientoDniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buscar_por_dni)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.BuscarVencimientoPorDni)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Navegación al Perfil del Administrador desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación al Menú Principal desde la barra inferior (Inicio)
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmBusquedaDni())
                .commit()
        }
    }

    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, FragmCuotasSocio())
            .addToBackStack(null)
            .commit()
    }
}
