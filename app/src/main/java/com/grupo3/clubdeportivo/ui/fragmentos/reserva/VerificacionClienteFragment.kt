package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.BusquedaDniFragment
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class VerificacionClienteFragment : Fragment(R.layout.fragment_verificacion_cliente) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private val dbHelper by lazy { DbHelperObject.get(requireContext()) }
    private val clienteServ by lazy { ClienteServ(ClienteRepo(dbHelper)) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        // Imprime en pantalla datos de clientes encontrados en BD
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.cliente.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombreCompleto
        view.findViewById<TextView>(R.id.tvPrintTipoCliente).text = viewModel.tipoCliente
        view.findViewById<TextView>(R.id.tvPrintAptoF).text =
            if (viewModel.cliente.tieneAptoFisico()) "Sí" else "No"

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnVolver.setOnClickListener {
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                BusquedaDniFragment(),
                EtapaReserva.BUSQUEDA_DNI
            )
        }

        btnContinuar.setOnClickListener {

            if (!viewModel.cliente.tieneAptoFisico()) {
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                )
                    .setMessage("Cliente NO puede realizar reserva sin apto físico. ¿Presenta apto físico?")
                    .setNegativeButton("No presenta apto físico") { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            "Finalizando reserva de cliente...",
                            Toast.LENGTH_SHORT
                        ).show()
                        (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                            BusquedaDniFragment(),
                            EtapaReserva.BUSQUEDA_DNI
                        )
                    }
                    .setPositiveButton("Sí presenta apto físico") { _, _ ->
                        if(clienteServ.actualizarAptoFisico(viewModel.cliente)) {
                            viewModel.cliente.aptoFisico = true
                        }
                        (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                            SeleccionActividadFragment(),
                            EtapaReserva.SELECCION_ACTIVIDAD
                        )
                    }
                    .show()
            } else {

                (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                    SeleccionActividadFragment(),
                    EtapaReserva.SELECCION_ACTIVIDAD
                )
            }
        }
    }
}