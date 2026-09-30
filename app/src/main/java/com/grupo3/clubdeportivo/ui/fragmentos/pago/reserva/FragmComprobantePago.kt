package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel

class FragmComprobantePago : Fragment(R.layout.fragment_comprobante_pago) {
    private val viewModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)

        btnContinuar.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de reserva...", Toast.LENGTH_LONG).show()
        }
    }
}