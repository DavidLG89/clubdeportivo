package com.grupo3.clubdeportivo.ui.fragments.reserva

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaReserva
import com.grupo3.clubdeportivo.ui.components.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.reserva.ReservaViewModel

class FragmBusquedaDni : Fragment(R.layout.fragment_busqueda_dni){
    private val viewModel: ReservaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Validar campo dni
        var edtDni = view.findViewById<TextInputEditText>(R.id.edtDniReserva)
        val ilDni = view.findViewById<TextInputLayout>(R.id.tilDniReserva)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)
        val tvError = view.findViewById<TextView>(R.id.tvError)


        btnBuscar.setOnClickListener {
            val dni = edtDni.text?.toString()?.trim() ?: ""

            when {
                dni.isEmpty() -> {
                    ilDni.error = ""
                    ilDni.errorIconDrawable = null
                    tvError.text = "Debe ingresar un DNI"
                }
                !dni.matches((Regex("^\\d{8}$"))) -> {
                    ilDni.error = ""
                    ilDni.errorIconDrawable = null
                    tvError.text = "El DNI debe tener 8 dígitos"
                }
                else -> {

                    viewModel.dni = dni
                    (requireActivity() as? BarraEstadoHost)?.avanzarA(
                        FragmVerificacionCliente(),
                        EtapaReserva.VERIFICACION_CLIENTE
                    )
                    tvError.visibility = GONE
                }
            }
        }
    }
}