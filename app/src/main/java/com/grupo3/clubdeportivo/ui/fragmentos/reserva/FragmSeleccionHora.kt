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
import com.google.android.material.card.MaterialCardView
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionHora : Fragment(R.layout.fragment_seleccion_hora) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private var itemSeleccionado: View? = null

    var cupoActual = 0
    var cupoRestante = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Incializa hora seleccionada
        viewModel.horaSeleccionada = null

        // Imprime actividad y fecha por pantalla
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad
        view.findViewById<TextView>(R.id.tvPrintFecha).text = viewModel.fecha

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnReservar = view.findViewById<Button>(R.id.btnReservar)
        val contenedor = view.findViewById<LinearLayout>(R.id.contenedorHorarios)

        val colorNormal = (contenedor.children.first() as MaterialCardView).cardBackgroundColor
        var itemSeleccionado: MaterialCardView? = null

        val colorSelecto = ContextCompat.getColor(requireContext(), R.color.color_boton_principal2)

        contenedor.children.filterIsInstance<MaterialCardView>().forEach { item ->


                val hora = item.findViewWithTag<TextView>("hora").text.toString()
                val cupo = item.findViewWithTag<TextView>("cupo").text.toString().toIntOrNull() ?: 0
                if(cupo <= 0) {item.alpha = 0.5f}

                item.setOnClickListener {
                if (cupo <= 0) {
                    Toast.makeText(
                        requireContext(), "No hay cupos disponibles en ese horario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                itemSeleccionado?.setBackgroundColor(Color.TRANSPARENT)
                item.setBackgroundColor(colorSelecto)
                itemSeleccionado = item
                viewModel.horaSeleccionada = hora
                cupoActual = cupo
                }
        }

        btnVolver.setOnClickListener {

            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
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


            cupoRestante = cupoActual - 1 // Cupo se guardará en la bd


            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                FragmCompReserva(),
                EtapaReserva.CONFIRMACION_RESERVA
            )
        }
    }
}
