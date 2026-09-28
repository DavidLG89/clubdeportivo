package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.fragmentos.reserva.FragmSeleccionActividad
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmFormalPago : Fragment(R.layout.fragment_formalizacion_pago) {
    private val viewModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val pagos = resources.getStringArray(R.array.metodos_pago)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, pagos)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autocompleteTV)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            viewModel.pago = pagos[position]
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.avanzarA(
                FragmVerificacionReserva(),
                EtapaPago.VERIFICACION
            )
        }

        btnContinuar.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Próximamente siguiente paso",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}