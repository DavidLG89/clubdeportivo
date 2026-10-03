package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

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


class FragmMetodoPago : Fragment(R.layout.fragment_metodo_pago) {
    private val viewModel: PagoResViewModel by activityViewModels()
    var metodoPagoSeleccionado: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintValor).text = viewModel.monto?.formatoPeso()


        val metodosPago = resources.getStringArray(R.array.metodos_pago)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, metodosPago)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutoMPago)

        autoCompleteTextView.setAdapter(arrayAdapter)

        viewModel.metodoPago?.takeIf { it.isEmpty() }?.let {
            metodoPagoSeleccionado = it
            autoCompleteTextView.setText(it, false)
        }

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            metodoPagoSeleccionado = metodosPago[position]
            viewModel.metodoPago = metodoPagoSeleccionado
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)


        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                FragmVerificacionReserva(),
                EtapaPago.VERIFICACION
            )
        }

        btnContinuar.setOnClickListener {
            if(metodoPagoSeleccionado == "") {
                Toast.makeText(requireContext(), "Debe seleccionar un método de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(metodoPagoSeleccionado == "Tarjeta Crédito" || metodoPagoSeleccionado == "MercadoPago") {
                (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                    FragmPagoEnCuotas(),
                    EtapaPago.PAGO_CUOTAS
                )
            } else {
                (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                    FragmComprobantePago(),
                    EtapaPago.COMPROBANTE_PAGO
                )
            }

        }
    }
}