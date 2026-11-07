package com.grupo3.clubdeportivo.ui.vencimiento

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Repositorios.CuotaRepo
import com.grupo3.clubdeportivo.clases.Servicios.CuotaServ
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.interfaces.ListenerDni
import java.util.Locale

/**
 * Actividad que gestiona la búsqueda del historial de cuotas de un socio mediante su DNI.
 *
 * Alberga el fragmento de búsqueda por DNI [BusquedaDniFragment] y navega hacia
 * [CuotasSocioFragment] al validar un DNI existente de socio en la base de datos.
 */
class BuscarVencimientoPorDniActivity : AppCompatActivity(), ListenerDni {

    private val viewModel: VencimientoDniViewModel by viewModels()
    private val dbHelper by lazy { DbHelperObject.get(this) }
    private val cuotaServ by lazy { CuotaServ(CuotaRepo(dbHelper), ClienteRepo(dbHelper)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buscar_por_dni)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.BuscarVencimientoPorDni)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del botón de retroceso
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
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

        // Carga inicial del fragmento de búsqueda por DNI
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BusquedaDniFragment())
                .commit()
        }
    }

    /**
     * Callback invocado cuando el DNI ha sido ingresado y validado en el fragmento de búsqueda.
     *
     * @param dni El número de documento de identidad ingresado.
     */
    override fun onDniValidado(dni: String) {
        val resultado = cuotaServ.obtenerCuotasSocioServ(dni)

        if (!resultado.ok) {
            Toast.makeText(this, resultado.mensaje, Toast.LENGTH_SHORT).show()
            return
        }

        cargarViewModel(dni, resultado.cuotas)

        // Reemplazo del contenedor con el fragmento de tabla de cuotas del socio
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CuotasSocioFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Carga los datos del socio y sus cuotas en el ViewModel compartido [VencimientoDniViewModel].
     *
     * @param dni Número de DNI validado.
     * @param cuotas Lista de objetos [CuotaDTO] obtenidos del servicio.
     */
    private fun cargarViewModel(dni: String, cuotas: List<CuotaDTO>) {
        val socio = cuotas.firstOrNull()?.socio

        viewModel.dni = dni
        viewModel.nombreSocio = "${socio?.nombre ?: ""} ${socio?.apellido ?: ""}".trim()
        viewModel.numeroSocio = socio?.idSocio?.toString() ?: ""
        viewModel.cuotasSocio = cuotas
            .sortedBy { it.fechaVencimiento }
            .map { c ->
                listOf(
                    c.idCuota.toString(),
                    FechaDb.formatearFecha(c.fechaVencimiento),
                    "$${"%,.0f".format(Locale("es", "AR"), c.montoCuota)}",
                    c.estadoCuota
                )
            }
    }

    /**
     * Color personalizado para la tarjeta del fragmento de búsqueda por DNI.
     */
    override val colorCard: Int
        get() = R.color.color_boton_principal2
}
