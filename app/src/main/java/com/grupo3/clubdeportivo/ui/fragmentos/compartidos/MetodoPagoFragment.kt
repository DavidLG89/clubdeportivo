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
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.clases.Repositorios.MetodoPagoRepo
import com.grupo3.clubdeportivo.clases.Servicios.ListadosMaestrosServ
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.interfaces.ListenerMetodoPago
class MetodoPagoFragment : Fragment(R.layout.fragment_metodo_pago) {

    private val listener get() = requireActivity() as ListenerMetodoPago
    private val dbHelper by lazy { DbHelperObject.get(requireContext()) }
    private val metodoPagoServ by lazy { ListadosMaestrosServ(MetodoPagoRepo(dbHelper)) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listenerComprobante = requireActivity() as? ListenerComprobantePago
        val dato = listenerComprobante?.datosComprobante()

        if (dato != null) {
            view.findViewById<TextView>(R.id.tvPrintDni)?.text = dato.dni ?: ""
            view.findViewById<TextView>(R.id.tvPrintNombre)?.text = dato.nombre ?: ""
            view.findViewById<TextView>(R.id.tvPrintActividad)?.text = dato.actividad ?: ""
            view.findViewById<TextView>(R.id.tvPrintValor)?.text = dato.monto?.let { "$ $it" } ?: ""
            view.findViewById<TextView>(R.id.tvPrintVenc)?.text = dato.fechaVencimiento ?: ""
        }

        val listaMetodoPago = metodoPagoServ.obtenerMetodosPago()
        val dropdownMetodos = view.findViewById<LinearLayout>(R.id.dropdownMetodo)
        val dropdownCuotas = view.findViewById<LinearLayout>(R.id.dropdownCuotas)
        val linearMetodoPago = view.findViewById<LinearLayout>(R.id.lMetodoPago)
        val textMetodo = view.findViewById<TextView>(R.id.tvPrintMetodoPago)

        dropdownCuotas?.visibility = View.GONE

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, listaMetodoPago.map { it.nombre })
        val autoCompletMetodos = view.findViewById<AutoCompleteTextView>(R.id.tvAutoMPago)
        autoCompletMetodos.setAdapter(adapter)

        // CAMBIO 1: .nombre
        listener.seleccionMetodoPago?.let { autoCompletMetodos.setText(it.nombre, false) }

        autoCompletMetodos.setOnItemClickListener { parent, _, position, _ ->
            val nombreElegido = parent.getItemAtPosition(position) as String
            val metodoElegido = listaMetodoPago.first { it.nombre == nombreElegido }

            listener.seleccionMetodoPago = metodoElegido

            dropdownMetodos.visibility = View.GONE

            textMetodo.text = metodoElegido.nombre
            linearMetodoPago.visibility = View.VISIBLE
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            listener.onMetodoPagoVolver()
        }

        // CAMBIO 3: == null en vez de isNullOrEmpty()
        btnContinuar.setOnClickListener {
            if (listener.seleccionMetodoPago == null) return@setOnClickListener
            listener.onMetodoPagoContinuar()
        }
    }
}