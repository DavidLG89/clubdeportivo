package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel

class FragmConfirmacionRegistroNoSocio : Fragment(R.layout.fragment_registro_cliente_paso1_confirmacion_nosocio) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ocultar la barra de estado/pasos de la Activity contenedora
        requireActivity().findViewById<View>(R.id.componentStepBar)?.visibility = View.GONE

        val tvTitulo = view.findViewById<TextView>(R.id.tvNombreConfirmacionNoSocio)
        val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleConfirmacionNoSocio)

        tvTitulo?.text = "Registro exitoso de\nCLIENTE"
        tvDetalle?.text = "NO SOCIO Nº 1: ${viewModel.nombre} ${viewModel.apellido}"

        view.findViewById<Button>(R.id.btnVolverNoSocio)?.setOnClickListener {
            val intent = Intent(requireContext(), MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
