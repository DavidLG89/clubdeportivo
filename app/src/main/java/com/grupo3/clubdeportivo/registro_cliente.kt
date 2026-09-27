package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class registro_cliente : AppCompatActivity() {

    // Variable para rastrear la selección de calidad de inscripción (null = no seleccionado)
    private var esSocioSeleccionado: Boolean? = null

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

        // Referencias a los campos de texto
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etApellido = findViewById<TextInputEditText>(R.id.etApellido)
        val etDni = findViewById<TextInputEditText>(R.id.etDni)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)

        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilApellido = findViewById<TextInputLayout>(R.id.tilApellido)
        val tilDni = findViewById<TextInputLayout>(R.id.tilDni)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)

        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)

        // Lógica de selección exclusiva (tipo RadioGroup) para los botones de imagen
        val btnNoSocio = findViewById<ImageView>(R.id.btnSelectionNoSocio)
        val btnSocio = findViewById<ImageView>(R.id.btnSelectionSocio)

        btnNoSocio.setOnClickListener {
            // Selecciona No Socio y deselecciona Socio
            btnNoSocio.setImageResource(R.drawable.img_boton_no_socio_sel)
            btnSocio.setImageResource(R.drawable.img_socio)
            esSocioSeleccionado = false
        }

        btnSocio.setOnClickListener {
            // Selecciona Socio y deselecciona No Socio
            btnSocio.setImageResource(R.drawable.img_boton_socio_sel)
            btnNoSocio.setImageResource(R.drawable.img_no_socio)
            esSocioSeleccionado = true
        }

        // Botón REGISTRAR con validaciones completas
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim() ?: ""
            val apellido = etApellido.text?.toString()?.trim() ?: ""
            val dni = etDni.text?.toString()?.trim() ?: ""
            val email = etEmail.text?.toString()?.trim() ?: ""

            // Limpiar errores previos
            tilNombre.error = null
            tilApellido.error = null
            tilDni.error = null
            tilEmail.error = null

            var hayError = false

            // Validar Nombre
            if (nombre.isEmpty()) {
                tilNombre.error = "Ingrese nombre"
                hayError = true
            }

            // Validar Apellido
            if (apellido.isEmpty()) {
                tilApellido.error = "Ingrese apellido"
                hayError = true
            }

            // Validar DNI (número y exactamente 8 dígitos)
            if (dni.isEmpty() || !dni.matches(Regex("^\\d{8}$"))) {
                tilDni.error = "El DNI debe ser numérico de 8 dígitos"
                hayError = true
            }

            // Validar Email (formato válido)
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Ingrese un email válido"
                hayError = true
            }

            // Validar que se haya seleccionado Socio o No Socio
            if (esSocioSeleccionado == null) {
                hayError = true
            }

            // Validar Checkbox Apto Físico
            if (!cbAptoFisico.isChecked) {
                hayError = true
            }

            if (hayError) {
                AlertDialog.Builder(this)
                    .setTitle("Error de registro")
                    .setMessage("No se puedo completar registro, revise errores, seleccione socio o no socio y tilde 'Presenta apto físico'")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Aceptar", null)
                    .show()
            } else {
                Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
                finish()
            }
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