package com.grupo3.clubdeportivo.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.grupo3.clubdeportivo.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout


import com.grupo3.clubdeportivo.reserva.ReservaViewModel

class ValidacionCliente : Fragment(R.layout.fragment_datos_cliente){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val valCliente = view.findViewById<LinearLayout>(R.id.cardContainerValidacion)
    }
}