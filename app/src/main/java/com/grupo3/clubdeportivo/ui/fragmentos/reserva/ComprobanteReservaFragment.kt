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
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.pagoReserva.PagoReservaActivity
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel
import com.grupo3.clubdeportivo.utils.formatoPeso
import kotlin.getValue

class ComprobanteReservaFragment : Fragment(R.layout.fragment_comprobante_reserva){
    private val viewModel: ReservaViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val reserva = viewModel.reserva ?: run {
            Toast.makeText(requireContext(), "No hay reserva para mostrar", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = viewModel.cliente
        val fecha = reserva.fechaHoraReserva
        val monto = if (reserva.estadoReserva == "Autorizada") "Sin cargo"
        else reserva.precio.formatoPeso()


        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvEditIdReserva).text = reserva.idReserva.toString().padStart(4, '0')
        view.findViewById<TextView>(R.id.tvPrintDni).text = reserva.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombreCompletoReserva
        view.findViewById<TextView>(R.id.tvPrintTipoCliente).text = viewModel.tipoCliente
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad.nombre
        view.findViewById<TextView>(R.id.tvPrintFecha).text = FechaDb.formatearFecha(fecha)
        view.findViewById<TextView>(R.id.tvPrintHorario).text = FechaDb.formatearHora(fecha)
        view.findViewById<TextView>(R.id.tvPrintValor).text = monto
        view.findViewById<TextView>(R.id.tvPrintFechaReserva).text = FechaDb.formatearFecha(viewModel.reserva!!.fechaHoraReserva)


        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)


        btnEnviar.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Enviando comprobante de reserva...",
                Toast.LENGTH_SHORT
            ).show()

            if (cliente is Socio) {
                val intent =
                    Intent(requireContext(), MenuPrincipalActivity::class.java)
                startActivity(intent)

            } else {
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                )
                    .setMessage("¿El cliente desea pagar la reserva ahora?")
                    .setNegativeButton("No paga") { _, _ ->

                        MaterialAlertDialogBuilder(
                            requireContext(),
                            R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                        )
                            .setMessage("Recuerde que la reserva queda pendiente de pago. Debe acercarse 15 minutos antes del inicio de la actividad para cancelar.")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                dialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    "Finalizando reserva de cliente...",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(requireContext(), MenuPrincipalActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                    .setPositiveButton("Paga") { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            "Redirigiendo a pago de reserva...",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent =
                            Intent(requireContext(), PagoReservaActivity::class.java).apply {
                                putExtra(PagoReservaActivity.EXTRA_DNI, reserva.dni)
                                putExtra(
                                    PagoReservaActivity.EXTRA_NOMBRE,
                                    viewModel.nombreCompleto
                                )
                                putExtra(
                                    PagoReservaActivity.EXTRA_ACTIVIDAD,
                                    reserva.actividad
                                )
                                putExtra(PagoReservaActivity.EXTRA_MONTO, reserva.precio)
                                putExtra(PagoReservaActivity.EXTRA_ID_RESERVA, reserva.idReserva)
                            }

                        startActivity(intent)
                    }
                    .show()
            }
        }
    }
}