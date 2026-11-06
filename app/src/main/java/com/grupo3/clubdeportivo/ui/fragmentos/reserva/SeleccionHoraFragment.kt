package com.grupo3.clubdeportivo.ui.fragmentos.reserva

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
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.clases.Repositorios.ProgramacionRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ReservaRepo
import com.grupo3.clubdeportivo.clases.Servicios.ProgramacionServ
import com.grupo3.clubdeportivo.clases.Servicios.ReservaServ
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SeleccionHoraFragment : Fragment(R.layout.fragment_seleccion_hora) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private val dbHelper by lazy { DbHelperObject.get(requireContext()) }
    private val reservaServ by lazy { ReservaServ(ReservaRepo(dbHelper)) }
    private val progServ by lazy { ProgramacionServ(ProgramacionRepo(dbHelper)) }
    private lateinit var rvProgramacion: RecyclerView
    private lateinit var txtSinProgramacion: TextView
    private lateinit var actividad: Actividad
    private lateinit var cliente: Cliente
    private lateinit var fecha: String
    private var programacionSeleccionada = Programacion()

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
            viewModel.programacion = programacionSeleccionada
            val txtError = view.findViewById<TextView>(R.id.tvError)
            txtError.visibility = View.GONE

            fun mostrarError(mensaje: String) {
                txtError.text = mensaje
                txtError.visibility = View.VISIBLE
            }

            val resultadoReserva =
                reservaServ.generarReserva(actividad, cliente, programacionSeleccionada.fechaHora)

            if (!resultadoReserva.ok) {
                mostrarError(resultadoReserva.mensaje)
                return@setOnClickListener
            }


            viewModel.reserva = resultadoReserva.reserva
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
        val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val salida = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val fechaDb = entrada.parse(fecha)?.let { salida.format(it) } ?: return
        val programacion = progServ.obtenerProgramacionPorActividad(actividad.id, fechaDb)



        rvProgramacion.adapter = ProgramacionAdapter(programacion) { seleccionada ->
            programacionSeleccionada = seleccionada
        }

        if (programacion.isEmpty()) {
            txtSinProgramacion.text = "Actividad sin horarios disponibles"
            rvProgramacion.visibility = View.GONE
            return
        } else {
            rvProgramacion.visibility = View.VISIBLE
        }
    }
}
