package com.grupo3.clubdeportivo.ui.vencimiento

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity

/**
 * Actividad menú principal del módulo de Vencimientos.
 *
 * Permite seleccionar entre las dos modalidades de consulta de vencimientos:
 * 1. Búsqueda de historial de cuotas por DNI de socio ([BuscarVencimientoPorDniActivity]).
 * 2. Consulta e impresión de cuotas vencidas en el día de hoy ([VencimientoDiarioActivity]).
 */
class VencimientosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vencimientos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vencimientos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón de retroceso
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Navegación a la búsqueda por DNI de socio
        val btnSocio = findViewById<Button>(R.id.btnBuscarSocio)
        btnSocio.setOnClickListener {
            val intent = Intent(this, BuscarVencimientoPorDniActivity::class.java)
            startActivity(intent)
        }

        // Navegación a la consulta de vencimientos del día
        val btnVencimiento = findViewById<Button>(R.id.btnVencimientoDiario)
        btnVencimiento.setOnClickListener {
            val intent = Intent(this, VencimientoDiarioActivity::class.java)
            startActivity(intent)
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
    }
}
