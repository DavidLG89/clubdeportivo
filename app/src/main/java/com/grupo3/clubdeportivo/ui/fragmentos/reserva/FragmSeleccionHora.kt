package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionHora : Fragment(R.layout.fragment_seleccion_hora) {
    private val viewModel: ReservaViewModel by activityViewModels()
        private var itemSeleccionado: View? = null

    var horaSeleccionada: String = ""
    var cupoActual = 0
    var cupoRestante = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnReservar = view.findViewById<Button>(R.id.btnContinuar)
        val contenedor = view.findViewById<LinearLayout>(R.id.contenedorHorarios)

        val colorSelecto = ContextCompat.getColor(requireContext(), R.color.color_boton_principal2)

        contenedor.children.forEach { item ->
            item.setOnClickListener {
                itemSeleccionado?.setBackgroundColor(Color.TRANSPARENT)
                item.setBackgroundColor(colorSelecto)
                itemSeleccionado = item

                val hora = view.findViewWithTag<TextView>("hora").text.toString()
                val cupo = view.findViewWithTag<TextView>("cupo").text.toString()

                val cupoDisponible = cupo.toInt()

                if (cupoDisponible <= 0) {
                    Toast.makeText(
                        requireContext(), "No hay cupos disponibles en ese horario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.horaSeleccionada = hora
                cupoActual = cupoDisponible
            }
        }

        btnVolver.setOnClickListener {

            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.avanzarA(
                FragmSeleccionFecha(),
                EtapaReserva.SELECCION_FECHA
            )
        }

        btnReservar.setOnClickListener {
            if (itemSeleccionado == null) {
                Toast.makeText(
                    requireContext(), "Debe seleccionar un horario",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            cupoRestante = cupoActual - 1

            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.avanzarA(
                FragmCompReserva(),
                EtapaReserva.CONFIRMACION_RESERVA
            )
        }
    }
}