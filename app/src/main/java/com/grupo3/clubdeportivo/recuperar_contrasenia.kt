package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class recuperar_contrasenia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_contrasenia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón Volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón Enviar email
        findViewById<Button>(R.id.btnEnviarEmail).setOnClickListener {
            Toast.makeText(this, "Se ha enviado el correo de recuperación", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Navegación Inicio
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
        //Sin navegacion porque no tiene sentido llevar a menu si no se logueó
        }

        // Navegación Perfil
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
        //Sin navegacion porque no tiene sentido llevar a perfil si no se logueó
        }
    }
}