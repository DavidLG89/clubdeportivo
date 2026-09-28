package com.grupo3.clubdeportivo.ui.fragments.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.components.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionHora : Fragment(R.layout.fragment_seleccion_hora){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnReservar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.avanzarA(
                FragmSeleccionFecha(),
                EtapaReserva.SELECCION_FECHA
            )
        }

        btnReservar.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.avanzarA(
                FragmCompReserva(),
                EtapaReserva.CONFIRMACION_RESERVA
            )
        }
    }
}