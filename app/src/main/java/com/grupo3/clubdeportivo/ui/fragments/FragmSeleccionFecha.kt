package com.grupo3.clubdeportivo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmSeleccionFecha : Fragment(R.layout.fragment_seleccion_fecha) {
    private val viewModel: ReservaViewModel by activityViewModels()
    private lateinit var edtData: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "Entré a FragmSeleccionFecha", Toast.LENGTH_SHORT).show()


        // Mostrar day picker
        edtData = view.findViewById(R.id.edtDate)

        edtData.setOnClickListener {
            showDatePickerDialog()
        }
    }
    private fun showDatePickerDialog() {
        val datePicker =
            DatePickerFragment({ day, month, year -> onDateSelected(day, month, year) })
        datePicker.show(childFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        val fechaTexto = "%02d/%02d/%04d".format(day, month + 1, year)
        edtData.setText(fechaTexto)
        viewModel.fechaHora = fechaTexto
    }

}
