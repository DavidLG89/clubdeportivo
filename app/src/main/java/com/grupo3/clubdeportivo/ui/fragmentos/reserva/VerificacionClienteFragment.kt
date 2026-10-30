package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class VerificacionClienteFragment : Fragment(R.layout.fragment_verificacion_cliente) {
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de clientes encontrados en BD
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintTipoCliente).text = viewModel.tipoCliente
        view.findViewById<TextView>(R.id.tvPrintAptoF).text = if(viewModel.aptoFisico) "Sí" else "No"

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                BusquedaDniFragment(),
                EtapaReserva.BUSQUEDA_DNI
            )
        }

        btnContinuar.setOnClickListener  {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                SeleccionActividadFragment(),
                EtapaReserva.SELECCION_ACTIVIDAD
            )
        }
    }
}