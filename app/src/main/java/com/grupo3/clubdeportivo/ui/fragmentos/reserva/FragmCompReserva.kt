package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.menu_principal
import com.grupo3.clubdeportivo.ui.pago.PagoReserva
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel
import kotlin.getValue

class FragmCompReserva : Fragment(R.layout.fragment_comprobante_reserva){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvPrintDni).text = viewModel.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombre
        view.findViewById<TextView>(R.id.tvPrintTipoCliente).text = viewModel.tipoCliente
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintFecha).text = viewModel.fecha
        view.findViewById<TextView>(R.id.tvPrintHorario).text = viewModel.horaSeleccionada

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)


        btnEnviar.setOnClickListener {
            Toast.makeText(requireContext(), "Enviando comprobante de reserva...", Toast.LENGTH_SHORT).show()
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

            )
                .setMessage(resources.getString(R.string.mensaje_pago))
                .setNegativeButton(resources.getString(R.string.no_paga)) { _, _ ->
                    MaterialAlertDialogBuilder(
                        requireContext(),
                        R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                    )
                        .setMessage(resources.getString(R.string.recordatorio))

                        .setPositiveButton(resources.getString(R.string.aceptar)) { dialog, _->
                            dialog.dismiss()
                        }
                        .show()
                }
                .setPositiveButton(resources.getString(R.string.paga)) { _, _ ->
                    Toast.makeText(requireContext(), "Redirigiendo a pago de reserva...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), PagoReserva::class.java)
                    startActivity(intent)
                }
                .show()

            Toast.makeText(requireContext(), "Finalizando reserva de cliente...", Toast.LENGTH_SHORT).show()
        }
    }
}