package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class menu_principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGestionPagos = findViewById<Button>(R.id.btnGestion_pagos)
        btnGestionPagos.setOnClickListener {
            val intent = Intent(this, gestion_pagos::class.java)
            startActivity(intent)
        }

        // Botón Registro Socio-No Socio
        findViewById<Button>(R.id.btnRegistro_socio_nosocio).setOnClickListener {
            val intent = Intent(this, registro_cliente::class.java)
            startActivity(intent)
        }

        // Navegación al Perfil del Administrador desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, perfil_administrador::class.java)
            startActivity(intent)
        }
    }
}