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
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoPagoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionActividad : Fragment(R.layout.fragment_seleccion_actividad) {
    private val viewModel: ReservaViewModel by activityViewModels()
    var actividadSeleccionada: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val actividades = resources.getStringArray(R.array.actividades)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, actividades)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autoActividad)

        autoCompleteTextView.setAdapter(arrayAdapter)

        viewModel.actividad?.takeIf { it.isEmpty() }?.let {
            actividadSeleccionada = it
            autoCompleteTextView.setText(it, false)
        }

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            actividadSeleccionada = actividades[position]
            viewModel.actividad = actividadSeleccionada
        }


        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)

        btnBuscar.setOnClickListener {

            if(actividadSeleccionada == "") {
                Toast.makeText(requireContext(), "Debe seleccionar una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (requireActivity() as? BarraEstadoPagoHost<EtapaReserva>)?.irA(
                FragmSeleccionFecha(),
                EtapaReserva.SELECCION_FECHA
            )
        }
    }
}
