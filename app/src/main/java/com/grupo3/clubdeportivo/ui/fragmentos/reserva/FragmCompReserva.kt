package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.FragmBusquedaDni
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmCompReserva : Fragment(R.layout.fragment_comprobante_reserva){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)


        btnEnviar.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de reserva...", Toast.LENGTH_LONG).show()
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

            )
                .setMessage(resources.getString(R.string.mensaje_pago))
                .setNegativeButton(resources.getString(R.string.no_paga)) { dialog, which ->
                    MaterialAlertDialogBuilder(
                        requireContext(),
                        R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                    )
                        .setMessage(resources.getString(R.string.recordatorio))

                        .setPositiveButton(resources.getString(R.string.aceptar)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
                .setPositiveButton(resources.getString(R.string.paga)) { dialog, which ->
                    Toast.makeText(requireContext(), "Redirigiendo a pago de reserva...", Toast.LENGTH_LONG).show()
                    //(requireActivity() as? BarraEstadoHost<EtapaPago>)?.avanzarA(
                    //    FragmBusquedaDni(),
                    //    EtapaPago.BUSQUEDA_DNI
                    //)
                }
                .show()

            Toast.makeText(requireContext(), "Finalizando reserva de cliente...", Toast.LENGTH_LONG).show()
        }
    }
}