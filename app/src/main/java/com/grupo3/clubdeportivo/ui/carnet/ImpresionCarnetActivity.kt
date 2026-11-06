package com.grupo3.clubdeportivo.ui.carnet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni

class ImpresionCarnetActivity : AppCompatActivity(), ListenerDni {

    private val viewModel: CarnetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_impresion_carnet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.impresion_carnet_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Navegación al Perfil desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil)?.setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación al Menú Principal desde la barra inferior
        findViewById<LinearLayout>(R.id.navInicio)?.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BusquedaDniFragment())
                .commit()
        }
    }

    override fun onDniValidado(dni: String) {
        val resultado = viewModel.buscarYProcesarCarnet(dni, this)
        when (resultado) {
            is ResultadoBusquedaCarnet.SocioNoExiste -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? BusquedaDniFragment
                fragment?.mostrarError("El socio con DNI $dni no existe.")
            }
            is ResultadoBusquedaCarnet.Error -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? BusquedaDniFragment
                fragment?.mostrarError(resultado.mensaje)
            }
            is ResultadoBusquedaCarnet.Exito -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmConfirmacionCarnet())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override val colorCard = R.color.color_boton_principal2
}
