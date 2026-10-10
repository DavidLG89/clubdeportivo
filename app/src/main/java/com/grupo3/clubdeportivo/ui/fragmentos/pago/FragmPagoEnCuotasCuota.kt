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
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel

class FragmPagoEnCuotasCuota : Fragment(R.layout.fragment_pago_en_cuotas_cuota_paso3_1) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()

    var cuota = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintValor).text = viewModel.monto?.toString() ?: ""
        view.findViewById<TextView>(R.id.tvPrintMetodoPago).text = viewModel.metodoPago

        val cuotas = resources.getStringArray(R.array.cuotas)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotas)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutoNCuotas)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            cuota = cuotas[position].toIntOrNull() ?: 0
            viewModel.cuota = cuota
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmMetodoPagoCuota(),
                EtapaPago.METODO_PAGO
            )
        }

        btnContinuar.setOnClickListener {
            when (cuota) {
                3 -> Toast.makeText(requireContext(), "Tiene 5% de descuento", Toast.LENGTH_SHORT).show()
                6 -> Toast.makeText(requireContext(), "Tiene 10% de descuento", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "NO tiene descuento", Toast.LENGTH_SHORT).show()
            }

            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmComprobantePagoCuota(),
                EtapaPago.COMPROBANTE_PAGO
            )
        }
    }
}
