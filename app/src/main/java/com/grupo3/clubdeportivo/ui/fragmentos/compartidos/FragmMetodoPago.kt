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
import com.grupo3.clubdeportivo.ui.componentes.ListenerMetodoPago

class FragmMetodoPago : Fragment(R.layout.fragment_metodo_pago) {

    private val listener get() = requireActivity() as ListenerMetodoPago

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val metodosPago = resources.getStringArray(R.array.metodos_pago)
        val cuotas = resources.getStringArray(R.array.cuotas)
        val dropdownCuotas = view.findViewById<LinearLayout>(R.id.dropdownCuotas)
        val dropdownMetodos = view.findViewById<LinearLayout>(R.id.dropdownMetodo)
        val linearMetodoPago = view.findViewById<LinearLayout>(R.id.lMetodoPago)
        var textMetodo = view.findViewById<TextView>(R.id.tvPrintMetodoPago)
        val linearNumCuotas = view.findViewById<LinearLayout>(R.id.linearCuotas)
        var textNumCuotas = view.findViewById<TextView>(R.id.tvPrintNumCuotas)

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, metodosPago)
        val autoCompletMetodos = view.findViewById<AutoCompleteTextView>(R.id.tvAutoMPago)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotas)
        val autoCompleteCuotas =
            view.findViewById<AutoCompleteTextView>(R.id.tvAutoNCuotas)
        autoCompletMetodos.setAdapter(adapter)
        autoCompleteCuotas.setAdapter(arrayAdapter)


        fun actualizarCuotas() {
            val metodo = listener.seleccionMetodoPago
            val esTarjeta = metodo == "Tarjeta Crédito" || metodo == "MercadoPago"
            dropdownCuotas.visibility = if (esTarjeta) View.VISIBLE else View.GONE
        }


        listener.seleccionMetodoPago?.let { autoCompletMetodos.setText(it, false) }
        actualizarCuotas()

        autoCompletMetodos.setOnItemClickListener { _, _, position, _ ->
            listener.seleccionMetodoPago = metodosPago[position]
            actualizarCuotas()
            dropdownMetodos.visibility = View.GONE
            textMetodo.text = listener.seleccionMetodoPago
            linearMetodoPago.visibility = View.VISIBLE
        }

        autoCompleteCuotas.setOnItemClickListener { _, _, position, _ ->
            listener.seleccionNumCuotas = cuotas[position].toIntOrNull() ?: 0
            textNumCuotas.text = listener.seleccionNumCuotas.toString()
            linearNumCuotas.visibility = View.VISIBLE
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