package com.grupo3.clubdeportivo.ui.fragmentos.compartidos.compartidosPago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.utils.formatoPeso
import java.util.Date

class ComprobantePagoFragment : Fragment(R.layout.fragment_comprobante_pago) {
    private val listener get() = requireActivity() as ListenerComprobantePago

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dato = listener.datosComprobante()

        view.findViewById<TextView>(R.id.tvEditIdPago)?.text = dato.idPago.padStart(4, '0')
        view.findViewById<TextView>(R.id.tvPrintValor)?.text = dato.monto!!.formatoPeso()
        view.findViewById<TextView>(R.id.tvPrintDni)?.text = dato.dni ?: ""
        view.findViewById<TextView>(R.id.tvPrintNombre)?.text = dato.nombre ?: ""
        view.findViewById<TextView>(R.id.tvPrintFechaPago).text = FechaDb.formatearFecha(Date())

        val layoutIdReserva = view.findViewById<LinearLayout>(R.id.layoutIdReserva)
        val tvIdReserva = view.findViewById<TextView>(R.id.tvIdReserva)
        val tvPrintIdReserva = view.findViewById<TextView>(R.id.tvPrintIdReserva)

        if (dato.esNoSocio || dato.idReserva.isNullOrEmpty()) {
            layoutIdReserva?.visibility = View.GONE
            tvIdReserva?.visibility = View.GONE
            tvPrintIdReserva?.visibility = View.GONE
        } else {
            layoutIdReserva?.visibility = View.VISIBLE
            tvIdReserva?.visibility = View.VISIBLE
            tvPrintIdReserva?.visibility = View.VISIBLE
            tvPrintIdReserva?.text = dato.idReserva
        }

        view.findViewById<TextView>(R.id.tvPrintActividad)?.text = dato.actividad ?: ""
        view.findViewById<TextView>(R.id.tvPrintMetodoPago)?.text = dato.metodoPago ?: ""
        view.findViewById<TextView>(R.id.tvPrintNumCuotas)?.text = dato.cuota?.toString() ?: "1"

        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)
        btnContinuar?.setOnClickListener {
            Toast.makeText(requireContext(), "Se envió comprobante al email", Toast.LENGTH_SHORT).show()
            listener.onComprobanteContinuar()
        }
    }
}
