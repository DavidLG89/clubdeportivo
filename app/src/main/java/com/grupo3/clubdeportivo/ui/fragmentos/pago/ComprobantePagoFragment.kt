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
import com.grupo3.clubdeportivo.ui.interfaces.ListenerComprobantePago
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoResViewModel

class ComprobantePagoFragment : Fragment(R.layout.fragment_comprobante_pago) {
    private val listener get() = requireActivity() as ListenerComprobantePago

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var dato = listener.datosComprobante()

        //view.findViewById<TextView>(R.id.tvPrintDni)?.text = dato.dni
        //view.findViewById<TextView>(R.id.tvPrintNombre)?.text = dato.nombre
        //view.findViewById<TextView>(R.id.tvPrintActividad)?.text = dato.actividad
        //view.findViewById<TextView>(R.id.tvPrintMetodoPago)?.text = dato.metodoPago
        //view.findViewById<TextView>(R.id.tvPrintNumCuotas)?.text = dato.cuota?.toString() ?: "-"

        val btnContinuar = view.findViewById<Button>(R.id.btnEnviar)
        btnContinuar.setOnClickListener {
            listener.onComprobanteContinuar()
        }
    }
}
