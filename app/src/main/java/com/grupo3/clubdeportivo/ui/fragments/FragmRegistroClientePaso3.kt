package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.components.BarraEstadoRegistroHost

class FragmRegistroClientePaso3 : Fragment(R.layout.fragment_registro_cliente_paso3) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvInfo = view.findViewById<TextView>(R.id.tvInfoClientePaso3)
        val tipoCliente = if (viewModel.esSocio == true) "Socio" else "No Socio"
        tvInfo.text = "Cliente: ${viewModel.nombre} ${viewModel.apellido}\nDNI: ${viewModel.dni}\nTipo: $tipoCliente\n\nFirma y aprobación del contrato."

        view.findViewById<Button>(R.id.btnVolverPaso3).setOnClickListener {
            (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                FragmRegistroClientePaso2(),
                EtapaRegistro.CUOTA_SOCIO
            )
        }

        view.findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            Toast.makeText(requireContext(), "Registro completado con éxito", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }
}
