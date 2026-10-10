package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost

class FragmConfirmacionRegistro : Fragment(R.layout.fragment_registro_cliente_paso1_confirmacion_socio) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreConfirmacion)
        val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleConfirmacion)

        val esSocio = viewModel.esSocio == true
        val tipoCliente = if (esSocio) "SOCIO" else "NO SOCIO"
        val numeroCliente = if (esSocio) "Nº 1: " else ": "

        tvNombre?.text = "Registro exitoso de CLIENTE"
        tvDetalle?.text = "$tipoCliente $numeroCliente${viewModel.nombre}, ${viewModel.apellido}"

        view.findViewById<Button>(R.id.btnContinuarConfirmacion)?.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaRegistro>)?.irA(
                FragmRegistroClientePaso2(),
                EtapaRegistro.CUOTA_SOCIO
            )
        }
    }
}
