package com.grupo3.clubdeportivo.ui.fragmentos.compartidos.compartidosPago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.Helpers.ValidacionDatos
import com.grupo3.clubdeportivo.ui.interfaces.ListenerPagoEfectivo
import com.grupo3.clubdeportivo.utils.formatoPeso

class PagoEfectivoFragment : Fragment(R.layout.fragment_pago_efectivo) {
    private val listener get() = requireActivity() as ListenerPagoEfectivo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val montoAPagar = listener.montoAPagar

        view.findViewById<TextView>(R.id.tvMontoPago).text = montoAPagar.formatoPeso()

        var pagado = false

        val btnPagar = view.findViewById<Button>(R.id.btnPagar)

        btnPagar.setOnClickListener {
            val etEfectivo = view.findViewById<TextInputEditText>(R.id.etEfectivo)
            val efectivo = etEfectivo.text?.toString()?.trim()


            val resultado = ValidacionDatos.validarMonto(efectivo)

            // Uso helper para validar monto
            if(!resultado.esValido) {
                Toast.makeText(
                    requireContext(), resultado.mensaje,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (pagado) {
                listener.onPagoEfectivoContinuar()
                return@setOnClickListener
            }

            val efectivoDouble = efectivo!!.toDouble()

            if (efectivoDouble < montoAPagar) {
                Toast.makeText(
                    requireContext(), "Efectivo debe ser igual o superior al monto a pagar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {

                if (!listener.registrarPago(efectivoDouble)) return@setOnClickListener

                val vuelto = efectivoDouble - montoAPagar


                if(vuelto >= 0.0) {
                    view.findViewById<TextView>(R.id.tvVuelto).visibility = View.VISIBLE

                    view.findViewById<TextView>(R.id.tvVuelto).text = vuelto.formatoPeso()
                    etEfectivo.isEnabled = false
                    pagado = true
                    btnPagar.text = "PAGAR"
                }
            }
        }
    }
}