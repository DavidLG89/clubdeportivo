package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Repositorios.ActividadRepo
import com.grupo3.clubdeportivo.clases.Servicios.ListadosMaestrosServ
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class SeleccionActividadFragment : Fragment(R.layout.fragment_seleccion_actividad) {
    private val viewModel: ReservaViewModel by activityViewModels()
       var actividadSeleccionada = Actividad()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Se inicializa el servicio
        val dbHelper = DbHelperObject.get(requireContext())
        val repositorio = ActividadRepo(dbHelper)
        val servicio = ListadosMaestrosServ(repositorio)

        val actividades = servicio.obtenerActividades()
        var listaActividades: MutableList<String> = mutableListOf()

        actividades.forEach { actividad ->
            listaActividades.add(actividad.nombre)
        }


        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, listaActividades)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autoActividad)

        autoCompleteTextView.setAdapter(arrayAdapter)

        viewModel.actividad?.let { actividad ->
            actividadSeleccionada = actividad
            autoCompleteTextView.setText(actividad.nombre, false)
        }

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            actividadSeleccionada = actividades[position]
            viewModel.actividad = actividadSeleccionada
        }


        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)

        btnBuscar.setOnClickListener {

            if(actividadSeleccionada.nombre == "" ) {
                Toast.makeText(requireContext(), "Debe seleccionar una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                SeleccionFechaFragment(),
                EtapaReserva.SELECCION_FECHA
            )
        }
    }
}
