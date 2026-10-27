package com.grupo3.clubdeportivo.ui

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
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Repositorios.LoginRepo
import com.grupo3.clubdeportivo.clases.Servicios.LoginServ
import com.grupo3.clubdeportivo.clases.base_datos_proced.InicializadorBD

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        val conexionBDInit = conexionBDsqlite()
        conexionBDInit.setContext(applicationContext)
        InicializadorBD.inicializarSiNoExiste(conexionBDInit, applicationContext)

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val tilUser = findViewById<TextInputLayout>(R.id.tilUser)
        val tilPass = findViewById<TextInputLayout>(R.id.tilPass)

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
                if (pass.isEmpty()) {
                    tilPass.error = "Complete contraseña"
                }

                AlertDialog.Builder(this)
                    .setTitle("Error de inicio de sesión")
                    .setMessage("Login incorrecto complete usuario y contraseña")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Aceptar", null)
                    .show()
            } else {
                val conexionBD = conexionBDsqlite()
                conexionBD.setContext(applicationContext)
                InicializadorBD.inicializarSiNoExiste(conexionBD, applicationContext)

                val loginRepo = LoginRepo(conexionBD)
                val loginServ = LoginServ(loginRepo)
                val usuario = loginServ.login(user, pass)

                if (usuario != null) {
                    Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    startActivity(intent)
                } else {
                    tilUser.error = "Usuario o contraseña incorrectos"
                    tilPass.error = "Usuario o contraseña incorrectos"

                    AlertDialog.Builder(this)
                        .setTitle("Error de inicio de sesión")
                        .setMessage("Usuario o contraseña incorrectos. Verifique sus credenciales.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            }
        }

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
        tvOlvide.text = spannable
        tvOlvide.setOnClickListener {
            val intent = Intent(this, RecuperarContraseniaActivity::class.java)
            startActivity(intent)
        }
    }
}
