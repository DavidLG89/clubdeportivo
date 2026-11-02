package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Repositorios.PagosRepo
import com.grupo3.clubdeportivo.clases.Servicios.PagoActividad
import com.grupo3.clubdeportivo.clases.Servicios.PagoCuotaServ
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoResViewModel

class PagoEfectivoFragment : Fragment(R.layout.fragment_pago_efectivo) {
    private val viewModel: PagoResViewModel by activityViewModels()
    private  val dbHelper by lazy { DbHelperObject.get(requireContext()) }
    private  val pagoServ by lazy { PagoActividad(PagosRepo(dbHelper)) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var vuelto: Double = 0.0

        val montoAPagar = viewModel.reservaSeleccionada?.precio

        if (montoAPagar == null) {
            Toast.makeText(
                requireContext(), "No existe monto asociado",
                Toast.LENGTH_SHORT
            ).show()
        }
        view.findViewById<TextView>(R.id.tvMontoPago).text = montoAPagar.toString()


        val btnPagar = view.findViewById<Button>(R.id.btnPagar)

        btnPagar.setOnClickListener {
            val etEfectivo = view.findViewById<TextInputEditText>(R.id.etEfectivo)
            etEfectivo.setText(viewModel.efectivo.toString())



            val efectivo = viewModel.efectivo
            if (efectivo == null) {
                Toast.makeText(
                    requireContext(), "Debe ingresar un monto válido",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (efectivo!! < montoAPagar!!) {
                Toast.makeText(
                    requireContext(), "Efectivo debe ser igual o superior al monto a pagar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (efectivo != 0.0) {
                    view.findViewById<TextView>(R.id.tvVuelto).visibility = View.VISIBLE
                    vuelto = efectivo - montoAPagar
                    view.findViewById<TextView>(R.id.tvVuelto).text = vuelto.toString()
                    val reserva = viewModel.reservaSeleccionada
                    val metodoPago = viewModel.metodoPago
                    if(reserva != null && metodoPago != null) {
                        pagoServ.registrarPago(reserva, montoAPagar, metodoPago)
                    }

                    Toast.makeText(
                        requireContext(), "El vuelto es de: $vuelto",
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }


        }


    }
}