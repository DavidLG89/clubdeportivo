package com.grupo3.clubdeportivo.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.grupo3.clubdeportivo.R

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

        val etEmail = findViewById<TextInputEditText>(R.id.etEmailRecuperar)

        // Botón Volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón Enviar email
        findViewById<Button>(R.id.btnEnviarEmail).setOnClickListener {
            val email = etEmail.text?.toString()?.trim() ?: ""

            when {
                email.isEmpty() -> {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Por favor, ingrese un correo electrónico.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Por favor, ingrese un correo electrónico válido.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
                else -> {
                    Toast.makeText(this, "Se ha enviado el correo de recuperación", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
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