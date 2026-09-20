package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class registro_cliente : AppCompatActivity() {
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

        // Lógica de selección exclusiva (tipo RadioGroup) para los botones de imagen
        val btnNoSocio = findViewById<android.widget.ImageView>(R.id.btnSelectionNoSocio)
        val btnSocio = findViewById<android.widget.ImageView>(R.id.btnSelectionSocio)

        btnNoSocio.setOnClickListener {
            // Selecciona No Socio y deselecciona Socio
            btnNoSocio.setImageResource(R.drawable.img_boton_no_socio_sel)
            btnSocio.setImageResource(R.drawable.img_socio)
        }

        btnSocio.setOnClickListener {
            // Selecciona Socio y deselecciona No Socio
            btnSocio.setImageResource(R.drawable.img_boton_socio_sel)
            btnNoSocio.setImageResource(R.drawable.img_no_socio)
        }

        // Navegación Inicio desde la barra inferior
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, menu_principal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        // Navegación Perfil desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, perfil_administrador::class.java)
            startActivity(intent)
        }
    }
}