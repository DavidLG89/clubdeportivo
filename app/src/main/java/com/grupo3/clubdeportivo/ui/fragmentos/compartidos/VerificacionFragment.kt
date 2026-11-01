package com.grupo3.clubdeportivo.ui.fragmentos.compartidos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.interfaces.ListenerVerificacion

class VerificacionFragment : Fragment(R.layout.fragment_verificacion) {

    private val listener get() = requireActivity() as ListenerVerificacion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<TextView>(R.id.tvTitulo).setText(listener.tituloRes)
        view.findViewById<TextInputLayout>(R.id.tilOpciones).hint = getString(listener.hintRes)

        // Traer DNI y nombre del cliente buscados en el paso anterior
        val tvPrintDni = view.findViewById<TextView>(R.id.tvPrintDni)
        val tvPrintNombre = view.findViewById<TextView>(R.id.tvPrintNombre)

        listener.dniCliente?.let { tvPrintDni?.text = it }
        listener.nombreCliente?.let { tvPrintNombre?.text = it }

        // Selecciona la reserva/cuota realizada
        val opciones = listener.opcionesLista ?: run {
            val opcionesRes = resources.getStringArray(listener.opcionRes)
            if (!listener.seleccion.isNullOrEmpty()) {
                arrayOf(listener.seleccion!!) + opcionesRes.filter { it != listener.seleccion }
            } else {
                opcionesRes
            }
        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, opciones)

        val auto = view.findViewById<AutoCompleteTextView>(R.id.autoOpciones)

        auto.setAdapter(arrayAdapter)

        listener.seleccion?.takeIf { !it.isEmpty() }?.let {
            auto.setText(it, false)
        }

        auto.setOnItemClickListener { _, _, position, _ ->
            listener.seleccion = opciones[position]
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            listener.onVerficiacionVolver()
        }

        btnContinuar.setOnClickListener {
            if(listener.seleccion.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe realizar una selecciónn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                listener.onVerificacionContinuar()
            }
        }
    }
}