package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.interfaces.ListenerPagoEfectivo

class PagoEfectivoFragment : Fragment(R.layout.fragment_pago_efectivo) {
    private val listener get() = requireActivity() as ListenerPagoEfectivo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val montoAPagar = listener.montoAPagar

        if (montoAPagar == null) {
            Toast.makeText(
                requireContext(), "No existe monto asociado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        view.findViewById<TextView>(R.id.tvMontoPago).text = montoAPagar.toString()


        var pagado = false

        val btnPagar = view.findViewById<Button>(R.id.btnPagar)

        btnPagar.setOnClickListener {
            val etEfectivo = view.findViewById<TextInputEditText>(R.id.etEfectivo)
            val efectivo = etEfectivo.text?.toString()?.toDoubleOrNull()

            if (pagado) {
                listener.onPagoEfectivoContinuar()
                return@setOnClickListener
            }

            if (efectivo == null || efectivo <= 0.0) {
                Toast.makeText(
                    requireContext(), "Debe ingresar un monto válido",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (efectivo <= montoAPagar) {
                Toast.makeText(
                    requireContext(), "Efectivo debe ser igual o superior al monto a pagar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {

                if (!listener.registrarPago(efectivo)) return@setOnClickListener

                val vuelto = efectivo - montoAPagar


                if(vuelto > 0.0) {
                    view.findViewById<TextView>(R.id.tvVuelto).visibility = View.VISIBLE

                    view.findViewById<TextView>(R.id.tvVuelto).text = "%.2f".format(vuelto)
                    etEfectivo.isEnabled = false
                    pagado = true
                    btnPagar.text = "PAGAR"
                }
            }
        }
    }
}