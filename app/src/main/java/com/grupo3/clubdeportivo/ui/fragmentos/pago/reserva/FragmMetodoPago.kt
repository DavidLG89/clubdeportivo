package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel


class FragmMetodoPago : Fragment(R.layout.fragment_metodo_pago) {
    private val viewModel: PagoResViewModel by activityViewModels()

    var metodoPago: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val metodosPago = resources.getStringArray(R.array.metodos_pago)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, metodosPago)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutocomplete)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            metodoPago = metodosPago[position]
            viewModel.pago = metodosPago[position]

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
            if(metodoPago == "") {
                Toast.makeText(
                    requireContext(),
                    "Debe seleccionar método de pago",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if(metodoPago == "Tarjeta Crédito" || metodoPago == "MercadoPago") {
                (requireActivity() as? BarraEstadoHost<EtapaPago>)?.avanzarA(
                    FragmCuotas(),
                    EtapaPago.PAGO_CUOTAS
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Paso a desarrollar próximamente",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }
}