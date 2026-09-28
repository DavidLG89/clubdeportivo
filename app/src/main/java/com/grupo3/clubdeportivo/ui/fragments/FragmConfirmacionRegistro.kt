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

class FragmConfirmacionRegistro : Fragment(R.layout.fragment_confirmacion_registro) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreConfirmacion)
        val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleConfirmacion)

        val tipoCliente = if (viewModel.esSocio == true) "Socio" else "No Socio"
        val aptoFisicoStr = if (viewModel.aptoFisico) "Sí" else "No"

        tvNombre.text = "Cliente: ${viewModel.nombre} ${viewModel.apellido}"
        tvDetalle.text = "DNI: ${viewModel.dni}\nEmail: ${viewModel.email}\nTipo: $tipoCliente\nApto Físico: $aptoFisicoStr"

        view.findViewById<Button>(R.id.btnVolverConfirmacion).setOnClickListener {
            (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                FragmRegistroClientePaso1(),
                EtapaRegistro.REGISTRO_DATOS_CLIENTE
            )
        }

        view.findViewById<Button>(R.id.btnContinuarConfirmacion).setOnClickListener {
            (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                FragmRegistroClientePaso2(),
                EtapaRegistro.CUOTA_SOCIO
            )
        }
    }
}
