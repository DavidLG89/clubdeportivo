package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionActividad : Fragment(R.layout.fragment_seleccion_actividad) {
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Toast.makeText(requireContext(), "Entra a selección actividad", Toast.LENGTH_LONG).show()
        // Validar campo actividad

        val ilActividad = view.findViewById<TextInputLayout>(R.id.tilActividad)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscarAct)
        val tvError = view.findViewById<TextView>(R.id.tvErrorAct)

        val actividades = resources.getStringArray(R.array.actividades)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, actividades)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autocompleteTV)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            viewModel.actividad = actividades[position]
        }


        btnBuscar.setOnClickListener {

            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.avanzarA(
                FragmSeleccionFecha(),
                EtapaReserva.SELECCION_FECHA
            )
        }
    }
}
