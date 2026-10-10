package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoResViewModel
import com.grupo3.clubdeportivo.utils.formatoPeso


class FragmPagoEnCuotas : Fragment(R.layout.fragment_pago_en_cuotas) {
    private val viewModel: PagoResViewModel by activityViewModels()

    var cuota = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintValor).text = viewModel.monto?.formatoPeso()
        view.findViewById<TextView>(R.id.tvPrintMetodoPago).text = viewModel.metodoPago

        val cuotas = resources.getStringArray(R.array.cuotas)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotas)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutoNCuotas)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

            cuota = cuotas[position].toIntOrNull()  ?: 0
            viewModel.cuota = cuota

        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmVerificacionReserva(),
                EtapaPago.METODO_PAGO
            )
        }

        btnContinuar.setOnClickListener {
            val cuotaSeleccionada = viewModel.cuota
            if(cuotaSeleccionada == null ) {
                Toast.makeText(requireContext(), "Debe seleccionar número de cuotas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val mensaje = when (cuotaSeleccionada){
                3 -> "Tiene 5% de descuento"
                6 -> "Tiene 3% de descuento"
                else -> "No tiene descuento"
            }

            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmComprobantePago(),
                EtapaPago.COMPROBANTE_PAGO
            )
        }
    }
}