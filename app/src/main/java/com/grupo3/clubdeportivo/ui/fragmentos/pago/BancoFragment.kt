package com.grupo3.clubdeportivo.ui.fragmentos.pago

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.data.model.EtapaPago
import com.grupo3.clubdeportivo.ui.fragmentos.compartidos.PagoEnCuotasFragment
import com.grupo3.clubdeportivo.ui.interfaces.BarraEstadoHost
import java.util.Calendar

class BancoFragment : Fragment(R.layout.fragment_pago_banco) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilTitular = view.findViewById<TextInputLayout>(R.id.tilTitular)
        val etTitular = view.findViewById<TextInputEditText>(R.id.etTitular)
        val tilDni = view.findViewById<TextInputLayout>(R.id.tilDni)
        val etDni = view.findViewById<TextInputEditText>(R.id.etDni)
        val tilBanco = view.findViewById<TextInputLayout>(R.id.tilBanco)
        val etBanco = view.findViewById<TextInputEditText>(R.id.etBanco)
        val tilCtaBco = view.findViewById<TextInputLayout>(R.id.tilCtaBco)
        val etCtaBco = view.findViewById<TextInputEditText>(R.id.etCtaBco)
        val tilTipoCuenta = view.findViewById<TextInputLayout>(R.id.tilTipoCuenta)
        val etTipoCuenta = view.findViewById<TextInputEditText>(R.id.etTipoCuenta)
        val tilFechaExp = view.findViewById<TextInputLayout>(R.id.tilFechaExp)
        val etFechaExp = view.findViewById<TextInputEditText>(R.id.etFechaExp)
        val tilCvv = view.findViewById<TextInputLayout>(R.id.tilCvv)
        val etCvv = view.findViewById<TextInputEditText>(R.id.etCvv)

        val btnPagar = view.findViewById<Button>(R.id.btnPagar) ?: view.findViewById<Button>(R.id.btnEnviar)

        btnPagar?.setOnClickListener {
            val titular = etTitular?.text?.toString()?.trim() ?: ""
            val dni = etDni?.text?.toString()?.trim() ?: ""
            val banco = etBanco?.text?.toString()?.trim() ?: ""
            val ctaBco = etCtaBco?.text?.toString()?.trim() ?: ""
            val tipoCuenta = etTipoCuenta?.text?.toString()?.trim() ?: ""
            val fechaExp = etFechaExp?.text?.toString()?.trim() ?: ""
            val cvv = etCvv?.text?.toString()?.trim() ?: ""

            tilTitular?.error = null
            tilDni?.error = null
            tilBanco?.error = null
            tilCtaBco?.error = null
            tilTipoCuenta?.error = null
            tilFechaExp?.error = null
            tilCvv?.error = null

            var isValid = true

            if (titular.isEmpty() || titular.length < 3) {
                tilTitular?.error = "Ingrese el nombre del titular (mínimo 3 caracteres)"
                isValid = false
            }

            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                tilDni?.error = "El DNI debe tener exactamente 8 dígitos"
                isValid = false
            }

            if (banco.length < 3) {
                tilBanco?.error = "El banco debe tener al menos 3 caracteres"
                isValid = false
            }

            if (ctaBco.length < 5 || !ctaBco.all { it.isDigit() }) {
                tilCtaBco?.error = "El número de cuenta debe tener al menos 5 dígitos"
                isValid = false
            }

            if (tipoCuenta.length < 5) {
                tilTipoCuenta?.error = "El tipo de cuenta debe tener al menos 5 caracteres"
                isValid = false
            }

            if (!isValidFutureDate(fechaExp)) {
                tilFechaExp?.error = "Fecha inválida o vencida (mayor a hoy, ej: 12/28)"
                isValid = false
            }

            if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
                tilCvv?.error = "El CVV debe tener exactamente 3 dígitos"
                isValid = false
            }

            if (!isValid) {
                Toast.makeText(requireContext(), "Por favor corrija los errores en el formulario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Datos validados correctamente", Toast.LENGTH_SHORT).show()
            (requireActivity() as? BarraEstadoHost<EtapaPago>)?.irA(
                PagoEnCuotasFragment(),
                EtapaPago.FORMALIZACION_PAGO
            )
        }
    }

    private fun isValidFutureDate(dateStr: String): Boolean {
        val parts = dateStr.split("/")
        if (parts.size != 2) return false
        val month = parts[0].toIntOrNull() ?: return false
        var year = parts[1].toIntOrNull() ?: return false
        if (month !in 1..12) return false
        if (year < 100) {
            year += 2000
        }

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH) + 1

        if (year > currentYear) return true
        if (year == currentYear && month > currentMonth) return true
        return false
    }
}
