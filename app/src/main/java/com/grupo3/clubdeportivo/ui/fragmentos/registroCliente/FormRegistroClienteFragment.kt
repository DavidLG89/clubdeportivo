package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Servicios.ClienteServ
import com.grupo3.clubdeportivo.data.model.EtapaRegistro
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel

class FormRegistroClienteFragment : Fragment(R.layout.fragment_form_registro_cliente) {

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
                    .setMessage("No se pudo completar el registro. Revise los errores y asegúrese de seleccionar Socio o No Socio.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Aceptar", null)
                    .show()
            } else {
                val esSocio = esSocioSeleccionado == true
                val dbHelper = DbHelperObject.get(requireContext())

                val clienteRepo = ClienteRepo(dbHelper)
                val clienteServ = ClienteServ(clienteRepo)

                val clienteNuevo = Cliente(nombre, apellido, dni, email, cbAptoFisico.isChecked)
                val resultado = clienteServ.realizarRegistro(clienteNuevo, esSocio)

                if (!resultado.ok) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("DNI existente o error de registro")
                        .setMessage("No se pudo registrar en la base de datos: ${resultado.mensaje}. Verifique si el DNI $dni ya se encuentra registrado.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Aceptar", null)
                        .show()
                } else {
                    viewModel.nombre = nombre
                    viewModel.apellido = apellido
                    viewModel.dni = dni
                    viewModel.email = email
                    viewModel.esSocio = esSocio
                    viewModel.aptoFisico = cbAptoFisico.isChecked
                    viewModel.clienteGuardado = resultado.cliente

                    val tipoTxt = if (esSocio) "Socio" else "No Socio"
                    Toast.makeText(
                        requireContext(),
                        "¡Guardado en base de datos SQLite como $tipoTxt (ID: ${resultado.cliente?.idCliente})!",
                        Toast.LENGTH_LONG
                    ).show()

                    (requireActivity() as? BarraEstadoHost<EtapaRegistro>)?.irA(
                        ConfirmacionRegistroFragment(),
                        EtapaRegistro.CONFIRMACION_REGISTRO
                    )
                }
            }
        }
    }
}
