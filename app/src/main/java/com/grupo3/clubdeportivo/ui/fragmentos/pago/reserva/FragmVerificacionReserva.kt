package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel

class FragmVerificacionReserva : Fragment(R.layout.fragment_verificacion_reserva) {
    private val viewModel: PagoResViewModel by activityViewModels()
    var actividadSeleccionada: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre

        // Selecciona la reserva realizada por cliente no socio para pago (lógica no desarrollada aún)
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

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmBusquedaDni(),
                EtapaPago.BUSQUEDA_DNI
            )
        }

        btnContinuar.setOnClickListener {
            if(viewModel.actividad.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe seleccionar una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmMetodoPago(),
                EtapaPago.METODO_PAGO
            )
        }
    }
}