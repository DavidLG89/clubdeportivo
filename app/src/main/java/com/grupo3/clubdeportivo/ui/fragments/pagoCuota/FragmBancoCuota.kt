package com.grupo3.clubdeportivo.ui.fragments.pagoCuota

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPagoCuota
import com.grupo3.clubdeportivo.ui.components.BarraEstadoPagoCuotaHost
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel

class FragmBancoCuota : Fragment(R.layout.fragment_pago_banco_cuota_paso3) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnContinuar = view.findViewById<Button>(R.id.btnPagar) ?: view.findViewById<Button>(R.id.btnEnviar)
        btnContinuar?.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de pago de cuota...", Toast.LENGTH_SHORT).show()
            (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                FragmComprobantePagoCuota(),
                EtapaPagoCuota.COMPROBANTE_PAGO
            )
        }
    }
}
