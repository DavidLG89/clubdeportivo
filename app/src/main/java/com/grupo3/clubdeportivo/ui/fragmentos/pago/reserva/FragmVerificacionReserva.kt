package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel

class FragmVerificacionReserva : Fragment(R.layout.fragment_verificacion_reserva) {
    private val viewModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val actividades = resources.getStringArray(R.array.actividades)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, actividades)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autocompleteTV)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            viewModel.actividad = actividades[position]
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.avanzarA(
                FragmBusquedaDni(),
                EtapaPago.BUSQUEDA_DNI
            )
        }

        btnContinuar.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.avanzarA(
                FragmMetodoPago(),
                EtapaPago.METODO_PAGO
            )
        }
    }
}