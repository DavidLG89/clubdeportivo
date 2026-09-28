package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.components.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionActividad : Fragment(R.layout.fragment_seleccion_actividad) {
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Validar campo actividad
        var edtActividad = view.findViewById<TextInputEditText>(R.id.edtActividad)
        val ilActividad = view.findViewById<TextInputLayout>(R.id.tilActividad)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscarAct)
        val tvError = view.findViewById<TextView>(R.id.tvErrorAct)

        btnBuscar.setOnClickListener {

            val actividad = edtActividad.text?.toString()?.trim() ?: ""

            if (actividad.isEmpty()) {
                ilActividad.error = ""
                ilActividad.errorIconDrawable = null
                tvError.text = "Debe ingresar una actividad"
            } else {
                ilActividad.error = null
                viewModel.actividad = actividad
                (requireActivity() as? BarraEstadoHost)?.avanzarA(
                    FragmSeleccionFecha(),
                    EtapaReserva.SELECCION_FECHA
                )
                tvError.visibility = GONE
                Toast.makeText(requireContext(), "Puede seleccionar fecha", Toast.LENGTH_LONG).show()
            }
        }
    }
}