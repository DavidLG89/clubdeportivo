package com.grupo3.clubdeportivo.ui.fragmentos.pago.reserva

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.menu_principal
import com.grupo3.clubdeportivo.ui.pago.PagoResViewModel
import com.grupo3.clubdeportivo.utils.formatoPeso

class FragmComprobantePago : Fragment(R.layout.fragment_comprobante_pago) {
    private val viewModel: PagoResViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintMetodoPago).text = viewModel.metodoPago
        view.findViewById<TextView>(R.id.tvPrintValor).text = viewModel.monto?.formatoPeso()
        if(viewModel.metodoPago == "Tarjeta Crédito" || viewModel.metodoPago == "MercadoPago") {
            view.findViewById<TextView>(R.id.tvPrintNumCuotas).text = viewModel.cuota.toString()
        } else {
            view.findViewById<TextView>(R.id.tvPrintNumCuotas).text = viewModel.sinCuota
        }


        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)

        btnContinuar.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de reserva...", Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), menu_principal::class.java)
            startActivity(intent)
        }
    }
}