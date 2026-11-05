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
        val fecha = reserva.fechaHora
        val monto = if (reserva.estadoReserva == "Autorizada") "Sin cargo"
        else reserva.precio.toInt().formatoPeso()
        val idFormateado = "%03d".format(reserva.idReserva)

        // Imprime en pantalla datos de la reserva
        view.findViewById<TextView>(R.id.tvEditIdReserva).text = idFormateado
        view.findViewById<TextView>(R.id.tvPrintDni).text = reserva.dni
        view.findViewById<TextView>(R.id.tvPrintNombre).text = viewModel.nombreCompletoReserva
        view.findViewById<TextView>(R.id.tvPrintTipoCliente).text = viewModel.tipoCliente
        view.findViewById<TextView>(R.id.tvPrintActividad).text = reserva.actividad
        view.findViewById<TextView>(R.id.tvPrintFecha).text = FechaDb.formatearFecha(fecha)
        view.findViewById<TextView>(R.id.tvPrintHorario).text = FechaDb.formatearHora(fecha)
        view.findViewById<TextView>(R.id.tvPrintValor).text = monto

        Toast.makeText(requireContext(), "Cupo restante: ${viewModel.programacion?.cuposDisponibles}", Toast.LENGTH_SHORT).show()
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
                    .setMessage(resources.getString(R.string.mensaje_pago))
                    .setNegativeButton(resources.getString(R.string.no_paga)) { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            "Finalizando reserva de cliente...",
                            Toast.LENGTH_SHORT
                        ).show()
                        MaterialAlertDialogBuilder(
                            requireContext(),
                            R.style.ThemeOverlay_App_MaterialAlertDialog_FullWidthButtons

                        )
                            .setMessage(resources.getString(R.string.recordatorio))

                            .setPositiveButton(resources.getString(R.string.aceptar)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    .setPositiveButton(resources.getString(R.string.paga)) { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            "Redirigiendo a pago de reserva...",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(requireContext(), PagoReservaActivity::class.java).apply {
                                putExtra(PagoReservaActivity.EXTRA_DNI, viewModel.reserva?.dni)
                                putExtra(
                                    PagoReservaActivity.EXTRA_NOMBRE,
                                    viewModel.nombreCompletoReserva
                                )
                                putExtra(
                                    PagoReservaActivity.EXTRA_ACTIVIDAD,
                                    viewModel.reserva?.actividad
                                )
                                putExtra(PagoReservaActivity.EXTRA_MONTO, viewModel.reserva?.precio)
                            }
                        startActivity(intent)
                    }
                    .show()
            }
        }
    }
}