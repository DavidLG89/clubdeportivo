package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionFecha : Fragment(R.layout.fragment_seleccion_fecha) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private lateinit var edtDate: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imprime actividad seleccionada en pantalla
        view.findViewById<TextView>(R.id.tvPrintActividad).text = viewModel.actividad


        // Mostrar date picker
        edtDate = view.findViewById(R.id.edtDate)

        edtDate.setText(viewModel.fecha)

        childFragmentManager.setFragmentResultListener("fecha", viewLifecycleOwner) {
            _, bundle ->
            onDateSelected(bundle.getInt("d"), bundle.getInt("m"), bundle.getInt("y"))
        }

        edtDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Pasar a la siguiente etapa
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)

        btnContinuar.setOnClickListener {
            if(viewModel.fecha.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (requireActivity() as? BarraEstadoHost<EtapaReserva>)?.irA(
                FragmSeleccionHora(),
                EtapaReserva.SELECCION_HORA
            )
        }
    }

    private fun showDatePickerDialog() {
        val datePicker =
            FragmDatePicker()
        datePicker.show(childFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        val fechaTexto = "%02d/%02d/%04d".format(day, month + 1, year)
        edtDate.setText(fechaTexto)
        viewModel.fecha = fechaTexto
    }

}
