package com.grupo3.clubdeportivo.ui.fragmentos.compartidos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.fragmentos.pago.ComprobantePagoFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago

class PagoEnCuotasFragment : Fragment(R.layout.fragment_pago_en_cuotas) {

    private val listenerComprobante get() = requireActivity() as ListenerComprobantePago
    private val listenerMetodo get() = requireActivity() as ListenerMetodoPago

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dato = listenerComprobante.datosComprobante()

        view.findViewById<TextView>(R.id.tvPrintDni)?.text = dato.dni ?: ""
        view.findViewById<TextView>(R.id.tvPrintNombre)?.text = dato.nombre ?: ""
        view.findViewById<TextView>(R.id.tvPrintActividad)?.text = dato.actividad ?: ""
        view.findViewById<TextView>(R.id.tvPrintValor)?.text = dato.monto?.let { "$ $it" } ?: ""
        view.findViewById<TextView>(R.id.tvPrintMetodoPago)?.text = dato.metodoPago ?: ""

        val cuotas = resources.getStringArray(R.array.cuotas)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotas)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvAutoNCuotas)

        autoCompleteTextView.setAdapter(arrayAdapter)

        listenerMetodo.seleccionNumCuotas?.let { num ->
            if (num > 0) {
                autoCompleteTextView.setText(num.toString(), false)
            }
        }

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val numSeleccionado = cuotas[position].toIntOrNull() ?: 0
            listenerMetodo.seleccionNumCuotas = numSeleccionado
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                MetodoPagoFragment(),
                EtapaPago.METODO_PAGO
            )
        }

        btnContinuar.setOnClickListener {
            val cuotaSeleccionada = listenerMetodo.seleccionNumCuotas
            if (cuotaSeleccionada == null || cuotaSeleccionada == 0) {
                Toast.makeText(requireContext(), "Debe seleccionar número de cuotas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mensaje = when (cuotaSeleccionada) {
                3 -> "Tiene 5% de descuento"
                6 -> "Tiene 10% de descuento"
                else -> "Sin descuento adicional"
            }
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                ComprobantePagoFragment(),
                EtapaPago.COMPROBANTE_PAGO
            )
        }
    }
}
