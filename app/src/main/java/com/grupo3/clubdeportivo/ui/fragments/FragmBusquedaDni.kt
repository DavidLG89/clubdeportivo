package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.reserva.Reserva
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmBusquedaDni : Fragment(R.layout.fragment_busqueda_dni){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnContinuar= view.findViewById<Button>(R.id.btnBuscar)


        btnContinuar.setOnClickListener {
            (requireActivity() as Reserva).avanzarA(
                FragmVerificacionCliente(),
                EtapaReserva.VERIFICACION_CLIENTE
            )
        }
    }
}