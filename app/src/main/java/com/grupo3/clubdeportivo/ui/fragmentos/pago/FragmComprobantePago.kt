package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoResViewModel
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoReservaActivity

class FragmComprobantePago : Fragment(R.layout.fragment_comprobante_pago) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()
    private val vModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvPrintDni)?.text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre)?.text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad)?.text = vModel.actividad
        view.findViewById<TextView>(R.id.tvPrintMetodoPago)?.text = viewModel.metodoPago
        view.findViewById<TextView>(R.id.tvPrintNumCuotas)?.text = viewModel.cuota?.toString() ?: "-"

        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)
        btnContinuar?.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de pago de cuota...", Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), MenuPrincipalActivity::class.java)
            startActivity(intent)
        }
    }
}
