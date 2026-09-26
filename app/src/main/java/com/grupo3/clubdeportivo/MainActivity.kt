package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            
            // Sumamos el margen del teclado al padding inferior del ScrollView para que se reduzca su tamaño real.
            // Si el teclado está abierto, usamos su tamaño; si no, el del sistema.
            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom
            
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val tilUser = findViewById<TextInputLayout>(R.id.tilUser)
        val tilPass = findViewById<TextInputLayout>(R.id.tilPass)

        // Buscar el botón por su ID y configurar el evento click
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            val user = etUser.text?.toString()?.trim() ?: ""
            val pass = etPass.text?.toString()?.trim() ?: ""

            tilUser.error = null
            tilPass.error = null

            if (user.isEmpty() || pass.isEmpty()) {
                if (user.isEmpty()) {
                    tilUser.error = "Complete usuario"
                }

                AlertDialog.Builder(this)
                    .setTitle("Error de inicio de sesión")
                    .setMessage("Login incorrecto complete usuario y contraseña")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Aceptar", null)
                    .show()
            } else {
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, menu_principal::class.java)
                startActivity(intent)
            }
        }

        // Dar formato de enlace a "ingresa aquí" y configurar el clic
        val tvOlvide = findViewById<TextView>(R.id.tvOlvideContrasenia)
        val text = "¿Olvidaste tu contraseña? ingresa aquí"
        val spannable = SpannableString(text)
        val target = "ingresa aquí"
        val start = text.indexOf(target)
        if (start != -1) {
            val end = start + target.length
            val linkColor = ContextCompat.getColor(this, R.color.color_boton_principal)
            spannable.setSpan(ForegroundColorSpan(linkColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tvOlvide.text = spannable
        tvOlvide.setOnClickListener {
            val intent = Intent(this, recuperar_contrasenia::class.java)
            startActivity(intent)
        }
    }
}