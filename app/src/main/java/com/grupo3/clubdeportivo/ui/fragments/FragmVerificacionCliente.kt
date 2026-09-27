package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmVerificacionCliente : Fragment(R.layout.fragment_datos_cliente){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val valCliente = view.findViewById<LinearLayout>(R.id.cardContainerValidacion)
    }
}