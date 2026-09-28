package com.grupo3.clubdeportivo.ui.fragments.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmCompReserva : Fragment(R.layout.fragment_comprobante_reserva){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)


        btnEnviar.setOnClickListener {
            //(requireActivity() as? BarraEstadoHost)?.avanzarA(
              //  FragmSeleccionActividad(),
                //EtapaReserva.SELECCION_ACTIVIDAD
            // )
            Toast.makeText(requireContext(), "Enviando comprobante...", Toast.LENGTH_LONG).show()
        }
    }
}