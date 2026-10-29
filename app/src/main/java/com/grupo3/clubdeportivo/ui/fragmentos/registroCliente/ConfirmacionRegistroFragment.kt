package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost

class ConfirmacionRegistroFragment : Fragment(R.layout.fragment_confirmacion_registro) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreConfirmacion)
        val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleConfirmacion)
        val btnAccion = view.findViewById<Button>(R.id.btnContinuarConfirmacion)

        val esSocio = viewModel.esSocio == true

        tvNombre?.text = getString(R.string.registro_exitoso_cliente)

        if (esSocio) {
            tvDetalle?.text = "SOCIO: ${viewModel.apellido}, ${viewModel.nombre}"
            btnAccion?.text = getString(R.string.continuar)
            btnAccion?.setOnClickListener {
                (requireActivity() as? BarraEstadoHost<EtapaRegistro>)?.irA(
                    DefinicionCuotaFragment(),
                    EtapaRegistro.CUOTA_SOCIO
                )
            }
        } else {
            tvDetalle?.text = "NO SOCIO: ${viewModel.apellido}, ${viewModel.nombre}"
            btnAccion?.text = getString(R.string.volver)
            btnAccion?.setOnClickListener {
                val intent = Intent(requireContext(), MenuPrincipalActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}
