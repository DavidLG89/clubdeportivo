package com.grupo3.clubdeportivo.ui.reserva

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoPagoHost
import com.grupo3.clubdeportivo.ui.componentes.ListenerDni
import com.grupo3.clubdeportivo.ui.componentes.CompBarraEstadoReserva
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmCompReserva
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmSeleccionActividad
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmSeleccionFecha
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmSeleccionHora
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmVerificacionCliente


class ReservaActivity : AppCompatActivity(), BarraEstadoPagoHost<EtapaReserva>, ListenerDni {

    private val viewModel: ReservaViewModel by viewModels()
    private lateinit var stepBar: CompBarraEstadoReserva

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece el layout de la activity
        setContentView(R.layout.activity_reserva)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reserva_actividades)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Obtiene Barra de Estado del layout
        stepBar = findViewById(R.id.componentStepBar)

        // Reconoce el fragmento actual al volver con el botón del celular
        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {
                is FragmBusquedaDni -> viewModel.irAPaso(EtapaReserva.BUSQUEDA_DNI)
                is FragmVerificacionCliente -> viewModel.irAPaso(EtapaReserva.VERIFICACION)
                is FragmSeleccionActividad -> viewModel.irAPaso(EtapaReserva.SELECCION_ACTIVIDAD)
                is FragmSeleccionFecha -> viewModel.irAPaso(EtapaReserva.SELECCION_FECHA)
                is FragmSeleccionHora -> viewModel.irAPaso(EtapaReserva.SELECCION_HORA)
                is FragmCompReserva -> viewModel.irAPaso(EtapaReserva.CONFIRMACION_RESERVA)
            }
        }
        // Botón Volver regresa al fragmento anterior
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        // Observa la etapa actual y actualiza la barra de estado
        viewModel.pasoActual.observe(this) { etapa ->
            setStep(etapa)
        }

        // Carga el primer fragmento al iniciar la activity
        if (savedInstanceState == null) {
            irA(FragmBusquedaDni(), EtapaReserva.BUSQUEDA_DNI)
        } // Navegación en footer
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

    // Actualiza la barra de estado
    override fun setStep(etapa: EtapaReserva) {
        stepBar.setStep(etapa)
    }

    // Navega en los distintos fragmentos de la activity
    override fun irA(fragment: Fragment, etapa: EtapaReserva) {
        val esPrimero = supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null

        viewModel.irAPaso(etapa)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .apply { if (!esPrimero) addToBackStack(null) }
            .commit()
    }

    // Si el dni está validado, continúa al paso siguiente y guarda el dato en viewModel
    override fun onDniValidado(dni: String) {
        viewModel.dni = dni
        irA(
            FragmVerificacionCliente(),
            EtapaReserva.VERIFICACION

        )
    }
}
