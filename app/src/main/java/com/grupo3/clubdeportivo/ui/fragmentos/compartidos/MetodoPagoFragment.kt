package com.grupo3.clubdeportivo.ui.fragmentos.compartidos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago

class MetodoPagoFragment : Fragment(R.layout.fragment_metodo_pago) {

    private val listener get() = requireActivity() as ListenerMetodoPago

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listenerComprobante = requireActivity() as? ListenerComprobantePago
        val dato = listenerComprobante?.datosComprobante()

        if (dato != null) {
            view.findViewById<TextView>(R.id.tvPrintDni)?.text = dato.dni ?: ""
            view.findViewById<TextView>(R.id.tvPrintNombre)?.text = dato.nombre ?: ""
            view.findViewById<TextView>(R.id.tvPrintActividad)?.text = dato.actividad ?: ""
            view.findViewById<TextView>(R.id.tvPrintValor)?.text = dato.monto?.let { "$ $it" } ?: ""
        }

        val metodosPago = resources.getStringArray(R.array.metodos_pago)
        val dropdownMetodos = view.findViewById<LinearLayout>(R.id.dropdownMetodo)
        val dropdownCuotas = view.findViewById<LinearLayout>(R.id.dropdownCuotas)
        val linearMetodoPago = view.findViewById<LinearLayout>(R.id.lMetodoPago)
        val textMetodo = view.findViewById<TextView>(R.id.tvPrintMetodoPago)

        // Ocultar sección de cuotas en este fragmento ya que se seleccionan en PagoEnCuotasFragment
        dropdownCuotas?.visibility = View.GONE

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, metodosPago)
        val autoCompletMetodos = view.findViewById<AutoCompleteTextView>(R.id.tvAutoMPago)
        autoCompletMetodos.setAdapter(adapter)

        listener.seleccionMetodoPago?.let { autoCompletMetodos.setText(it, false) }

        autoCompletMetodos.setOnItemClickListener { _, _, position, _ ->
            listener.seleccionMetodoPago = metodosPago[position]
            dropdownMetodos.visibility = View.GONE
            textMetodo.text = listener.seleccionMetodoPago
            linearMetodoPago.visibility = View.VISIBLE
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            listener.onMetodoPagoVolver()
        }

        btnContinuar.setOnClickListener {
            if (listener.seleccionMetodoPago.isNullOrEmpty()) {
                return@setOnClickListener
            } else {
                listener.onMetodoPagoContinuar()
            }
        }
    }
}
