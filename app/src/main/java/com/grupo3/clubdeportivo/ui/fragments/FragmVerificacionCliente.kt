package com.grupo3.clubdeportivo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.components.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmVerificacionCliente : Fragment(R.layout.fragment_datos_cliente){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val valCliente = view.findViewById<LinearLayout>(R.id.cardContainerValidacion)

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost)?.avanzarA(
                FragmBusquedaDni(),
                EtapaReserva.BUSQUEDA_DNI
            )
        }
    }
}