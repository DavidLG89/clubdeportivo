package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel

class BancoFragment : Fragment(R.layout.fragment_pago_banco) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnContinuar = view.findViewById<Button>(R.id.btnPagar) ?: view.findViewById<Button>(R.id.btnEnviar)
        btnContinuar?.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de pago de cuota...", Toast.LENGTH_SHORT).show()
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                ComprobantePagoFragment(),
                EtapaPago.COMPROBANTE_PAGO
            )
        }
    }
}
