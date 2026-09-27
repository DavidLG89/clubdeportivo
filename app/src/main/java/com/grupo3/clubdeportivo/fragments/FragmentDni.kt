package com.grupo3.clubdeportivo.fragments

import android.os.Bundle
import android.view.View
import com.grupo3.clubdeportivo.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout


import com.grupo3.clubdeportivo.reserva.ReservaViewModel

class FragmentDni : Fragment(R.layout.fragment_busqueda_dni){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bdni = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
    }
}