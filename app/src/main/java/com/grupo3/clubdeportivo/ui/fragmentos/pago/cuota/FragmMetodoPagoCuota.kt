package com.grupo3.clubdeportivo.ui.fragmentos.pago.cuota

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPagoCuota
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoPagoCuotaHost
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel

class FragmMetodoPagoCuota : Fragment(R.layout.fragment_metodo_pago_cuota_paso3) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()

    var metodoPago: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintValor).text = viewModel.monto?.toString() ?: ""

        val metodosPago = resources.getStringArray(R.array.metodos_pago)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, metodosPago)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutoMPago)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            metodoPago = metodosPago[position]
            viewModel.metodoPago = metodosPago[position]
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                FragmVerificacionCuota(),
                EtapaPagoCuota.VERIFICACION
            )
        }

        btnContinuar.setOnClickListener {
            if (metodoPago.isEmpty()) {
                return@setOnClickListener
            }

            if (metodoPago == "Tarjeta Crédito" || metodoPago == "MercadoPago") {
                (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                    FragmPagoEnCuotasCuota(),
                    EtapaPagoCuota.PAGO_CUOTAS
                )
            } else {
                (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                    FragmComprobantePagoCuota(),
                    EtapaPagoCuota.COMPROBANTE_PAGO
                )
            }
        }
    }
}
