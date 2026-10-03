package com.grupo3.clubdeportivo.ui.registroCliente

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

class RegistroClienteMensajeAceptacionContrato : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_cliente_mensaje_aceptacion_contrato)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón NO -> abre pantalla de contrato NO aceptado
        findViewById<Button>(R.id.btnNo).setOnClickListener {
            val intent = Intent(this, RegistroClienteMensajeNo::class.java)
            startActivity(intent)
        }

        // Botón SÍ -> abre pantalla de contrato SÍ aceptado
        findViewById<Button>(R.id.btnSi).setOnClickListener {
            val intent = Intent(this, RegistroClienteMensajeSi::class.java)
            startActivity(intent)
        }

        // Navegación en footer
        findViewById<LinearLayout>(R.id.navInicio)?.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.navPerfil)?.setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }
    }
}
