package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.componentes.BarraEstadoHost

class FragmRegistroClientePaso1 : Fragment(R.layout.fragment_form_registro_cliente) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()
    private var esSocioSeleccionado: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asegurar que la barra de estado esté visible al cargar el Paso 1
        requireActivity().findViewById<View>(R.id.componentStepBar)?.visibility = View.VISIBLE

        val etNombre = view.findViewById<TextInputEditText>(R.id.etNombre)
        val etApellido = view.findViewById<TextInputEditText>(R.id.etApellido)
        val etDni = view.findViewById<TextInputEditText>(R.id.etDni)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)

        val tilNombre = view.findViewById<TextInputLayout>(R.id.tilNombre)
        val tilApellido = view.findViewById<TextInputLayout>(R.id.tilApellido)
        val tilDni = view.findViewById<TextInputLayout>(R.id.tilDni)
        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)

        val cbAptoFisico = view.findViewById<CheckBox>(R.id.cbAptoFisico)

        val btnNoSocio = view.findViewById<ImageView>(R.id.btnSelectionNoSocio)
        val btnSocio = view.findViewById<ImageView>(R.id.btnSelectionSocio)

        // Prepopulate if model already has values
        etNombre.setText(viewModel.nombre)
        etApellido.setText(viewModel.apellido)
        etDni.setText(viewModel.dni)
        etEmail.setText(viewModel.email)
        cbAptoFisico.isChecked = viewModel.aptoFisico

        esSocioSeleccionado = viewModel.esSocio
        if (esSocioSeleccionado == true) {
            btnSocio.setImageResource(R.drawable.img_boton_socio_sel)
            btnNoSocio.setImageResource(R.drawable.img_no_socio)
        } else if (esSocioSeleccionado == false) {
            btnNoSocio.setImageResource(R.drawable.img_boton_no_socio_sel)
            btnSocio.setImageResource(R.drawable.img_socio)
        }

        btnNoSocio.setOnClickListener {
            btnNoSocio.setImageResource(R.drawable.img_boton_no_socio_sel)
            btnSocio.setImageResource(R.drawable.img_socio)
            esSocioSeleccionado = false
        }

        btnSocio.setOnClickListener {
            btnSocio.setImageResource(R.drawable.img_boton_socio_sel)
            btnNoSocio.setImageResource(R.drawable.img_no_socio)
            esSocioSeleccionado = true
        }

        view.findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim() ?: ""
            val apellido = etApellido.text?.toString()?.trim() ?: ""
            val dni = etDni.text?.toString()?.trim() ?: ""
            val email = etEmail.text?.toString()?.trim() ?: ""

            tilNombre.error = null
            tilApellido.error = null
            tilDni.error = null
            tilEmail.error = null

            var hayError = false

            if (nombre.isEmpty()) {
                tilNombre.error = "Ingrese nombre"
                hayError = true
            }

            if (apellido.isEmpty()) {
                tilApellido.error = "Ingrese apellido"
                hayError = true
            }

            if (dni.isEmpty() || !dni.matches(Regex("^\\d{8}$"))) {
                tilDni.error = "El DNI debe ser numérico de 8 dígitos"
                hayError = true
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Ingrese un email válido"
                hayError = true
            }

            if (esSocioSeleccionado == null) {
                hayError = true
            }


            if (hayError) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error de registro")
                    .setMessage("No se pudo completar registro, revise errores, seleccione socio o no socio.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Aceptar", null)
                    .show()
            } else {
                viewModel.nombre = nombre
                viewModel.apellido = apellido
                viewModel.dni = dni
                viewModel.email = email
                viewModel.esSocio = esSocioSeleccionado
                viewModel.aptoFisico = cbAptoFisico.isChecked

                if (esSocioSeleccionado == false) {
                    (requireActivity() as? BarraEstadoHost<EtapaRegistro>)?.irA(
                        FragmConfirmacionRegistroNoSocio(),
                        EtapaRegistro.CONFIRMACION_REGISTRO
                    )
                } else {
                    (requireActivity() as? BarraEstadoHost<EtapaRegistro>)?.irA(
                        FragmConfirmacionRegistro(),
                        EtapaRegistro.CONFIRMACION_REGISTRO
                    )
                }
            }
        }
    }
}
