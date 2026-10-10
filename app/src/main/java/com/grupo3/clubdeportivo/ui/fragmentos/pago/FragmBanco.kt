package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoResViewModel

class FragmBanco : Fragment(R.layout.fragment_pago_banco) {
    private val viewModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)

        btnContinuar.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de reserva...", Toast.LENGTH_LONG).show()
        }
    }
}