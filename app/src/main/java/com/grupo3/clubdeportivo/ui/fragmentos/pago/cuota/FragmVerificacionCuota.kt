package com.grupo3.clubdeportivo.ui.fragmentos.pago.cuota

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoPagoCuotaHost
import com.grupo3.clubdeportivo.ui.pagoCuota.PagoCuotaViewModel

class FragmVerificacionCuota : Fragment(R.layout.fragment_verificacion_cuota_paso2) {
    private val viewModel: PagoCuotaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre

        val cuotasPendientes = arrayOf("1")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, cuotasPendientes)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autoActividad)
        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            viewModel.actividad = "Cuota " + cuotasPendientes[position]
        }

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                FragmBusquedaDniCuota(),
                EtapaPago.BUSQUEDA_DNI
            )
        }

        btnContinuar.setOnClickListener {
            (requireActivity() as? BarraEstadoPagoCuotaHost)?.avanzarA(
                FragmMetodoPagoCuota(),
                EtapaPago.METODO_PAGO
            )
        }
    }
}
