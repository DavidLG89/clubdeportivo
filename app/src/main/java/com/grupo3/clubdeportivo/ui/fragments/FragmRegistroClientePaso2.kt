package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.components.BarraEstadoRegistroHost

class FragmRegistroClientePaso2 : Fragment(R.layout.fragment_registro_cliente_paso2) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvInfo = view.findViewById<TextView>(R.id.tvInfoClientePaso2)
        val tipoCliente = if (viewModel.esSocio == true) "Socio" else "No Socio"
        tvInfo.text = "Cliente: ${viewModel.nombre} ${viewModel.apellido}\nDNI: ${viewModel.dni}\nTipo: $tipoCliente\n\nGeneración de cuota de inscripción."

        view.findViewById<Button>(R.id.btnVolverPaso2).setOnClickListener {
            (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                FragmConfirmacionRegistro(),
                EtapaRegistro.CONFIRMACION_REGISTRO
            )
        }

        view.findViewById<Button>(R.id.btnContinuarPaso2).setOnClickListener {
            (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                FragmRegistroClientePaso3(),
                EtapaRegistro.CONTRATO
            )
        }
    }
}
