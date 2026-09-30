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


class FragmCuotas : Fragment(R.layout.fragment_pago_cuotas) {
    private val viewModel: PagoResViewModel by activityViewModels()

    var cuota = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val cuotas = resources.getStringArray(R.array.cuotas)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotas)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutocomplete)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            cuota = cuotas[position].toIntOrNull()  ?: 0
            viewModel.cuota = cuota

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
            when{
                cuota == 3 -> {
                    Toast.makeText(
                        requireContext(),
                        "Tiene 5% de descuento",
                        Toast.LENGTH_SHORT).show()
                }

                cuota == 6 -> {
                    Toast.makeText(
                        requireContext(),
                        "Tiene 10% de descuento",
                        Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "NO tiene descuento",
                        Toast.LENGTH_SHORT).show()
                }
            }

            Toast.makeText(
                requireContext(),
                "Próximamente siguiente paso",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}