package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.components.BarraEstadoRegistroHost

class FragmRegistroClientePaso2 : Fragment(R.layout.fragment_registro_cliente_paso2) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDni = view.findViewById<TextView>(R.id.tvDniPaso2)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombrePaso2)
        val tvTipoCliente = view.findViewById<TextView>(R.id.tvTipoClientePaso2)
        val tvAptoFisico = view.findViewById<TextView>(R.id.tvAptoFisicoPaso2)

        val etMontoCuota = view.findViewById<TextInputEditText>(R.id.etMontoCuota)
        val tilMontoCuota = view.findViewById<TextInputLayout>(R.id.tilMontoCuota)

        val tipoClienteStr = if (viewModel.esSocio == true) "Socio" else "No Socio"
        val aptoFisicoStr = if (viewModel.aptoFisico) "Sí" else "No"

        tvDni?.text = Html.fromHtml("<b>DNI:</b> ${viewModel.dni}", Html.FROM_HTML_MODE_LEGACY)
        tvNombre?.text = Html.fromHtml("<b>Nombre:</b> ${viewModel.nombre} ${viewModel.apellido}", Html.FROM_HTML_MODE_LEGACY)
        tvTipoCliente?.text = Html.fromHtml("<b>Tipo de cliente:</b> $tipoClienteStr", Html.FROM_HTML_MODE_LEGACY)
        tvAptoFisico?.text = Html.fromHtml("<b>Apto Físico:</b> $aptoFisicoStr", Html.FROM_HTML_MODE_LEGACY)

        if (viewModel.montoCuota.isNotEmpty()) {
            etMontoCuota?.setText(viewModel.montoCuota)
        }

        view.findViewById<Button>(R.id.btnContinuarPaso2)?.setOnClickListener {
            val monto = etMontoCuota?.text?.toString()?.trim() ?: ""
            tilMontoCuota?.error = null

            if (monto.isEmpty()) {
                tilMontoCuota?.error = "Ingrese monto"
            } else {
                viewModel.montoCuota = monto
                (requireActivity() as? BarraEstadoRegistroHost)?.avanzarA(
                    FragmRegistroClientePaso3(),
                    EtapaRegistro.CONTRATO
                )
            }
        }
    }
}
