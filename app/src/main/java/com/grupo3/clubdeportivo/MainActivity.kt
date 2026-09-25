package com.grupo3.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Buscar el botón por su ID y configurar el evento click
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            val intent = Intent(this, menu_principal::class.java)
            startActivity(intent)
        }

        // Ver/ ocultar texto contraseña
        val btnAccion = findViewById<ToggleButton>(R.id.btnAccion)
        val etPass = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPass)

        btnAccion.setOnClickListener {
            if(btnAccion.isChecked) {
                btnAccion.text = "Ocultar"
                etPass.inputType = android.text.InputType.TYPE_CLASS_TEXT
            } else {
                btnAccion.text = "Ver"
                etPass.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}