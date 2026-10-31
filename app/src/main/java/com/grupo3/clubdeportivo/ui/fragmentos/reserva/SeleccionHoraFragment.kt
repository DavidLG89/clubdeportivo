package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Repositorios.ProgramacionRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ReservaRepo
import com.grupo3.clubdeportivo.clases.Servicios.ProgramacionServ
import com.grupo3.clubdeportivo.clases.Servicios.ReservaServ
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SeleccionHoraFragment : Fragment(R.layout.fragment_seleccion_hora) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private lateinit var dbHelper: DbHelper
    private lateinit var repositorio: ReservaRepo
    private lateinit var servicio: ReservaServ
    private lateinit var repoProgramacion: ProgramacionRepo
    private lateinit var servProgramacion: ProgramacionServ
    private lateinit var rvProgramacion: RecyclerView
    private lateinit var txtSinProgramacion: TextView
    private lateinit var actividad: Actividad

    private lateinit var cliente: Cliente

    private lateinit var fecha: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rvProgramacion = view.findViewById(R.id.rvProgramacion)
        txtSinProgramacion = view.findViewById(R.id.txtSinProgramacion)

        rvProgramacion.layoutManager = LinearLayoutManager(requireContext())


        actividad = viewModel.actividad
        fecha = viewModel.fecha
        cliente = viewModel.cliente


        // Imprime actividad y fecha por pantalla
        view.findViewById<TextView>(R.id.tvPrintActividad).text = actividad.nombre
        view.findViewById<TextView>(R.id.tvPrintFecha).text = fecha

        cargarProgramacion()

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnReservar = view.findViewById<Button>(R.id.btnReservar)


        btnVolver.setOnClickListener {

            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                SeleccionFechaFragment(),
                EtapaReserva.SELECCION_FECHA
            )
        }

        btnReservar.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                ComprobanteReservaFragment(),
                EtapaReserva.CONFIRMACION_RESERVA
            )
        }
    }
    override fun onResume() {
        super.onResume()
        cargarProgramacion()
    }
    private fun cargarProgramacion() {
        dbHelper = DbHelperObject.get(requireContext())
        repoProgramacion = ProgramacionRepo(dbHelper)
        servProgramacion = ProgramacionServ(repoProgramacion)

        val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val salida = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val fechaDb = entrada.parse(fecha)?.let { salida.format(it) } ?: return
        val programacion = servProgramacion.obtenerProgramacionPorActividad(actividad.id, fechaDb)
        rvProgramacion.adapter = ProgramacionAdapter(programacion)

        Toast.makeText(
            requireContext(),
            "Programaciones encontradas: ${programacion.size}",
            Toast.LENGTH_LONG
        ).show()

        if(programacion.isEmpty()) {
            txtSinProgramacion.visibility = View.VISIBLE
            rvProgramacion.visibility = View.GONE
        } else {
            txtSinProgramacion.visibility = View.GONE
            rvProgramacion.visibility = View.VISIBLE
        }


    }
}
