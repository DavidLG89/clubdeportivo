package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteMensajeAceptacionContrato
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmRegistroClientePaso3 : Fragment(R.layout.fragment_registro_contrato) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFecha = view.findViewById<TextView>(R.id.tvFechaContrato)
        val tvParrafo1 = view.findViewById<TextView>(R.id.tvParrafo1Contrato)
        val tvParrafo2 = view.findViewById<TextView>(R.id.tvParrafo2Contrato)
        val tvParrafo3 = view.findViewById<TextView>(R.id.tvParrafo3Contrato)
        val tvParrafo4 = view.findViewById<TextView>(R.id.tvParrafo4Contrato)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaHoyStr = dateFormat.format(calendar.time)
        val diaMes = calendar.get(Calendar.DAY_OF_MONTH)

        val montoStr = if (viewModel.montoCuota.isNotEmpty()) "$${viewModel.montoCuota}" else "$10.000"

        tvFecha?.text = "Fecha: $fechaHoyStr"

        tvParrafo1?.text = "Se deja constancia que el cliente ${viewModel.nombre} ${viewModel.apellido}, DNI ${viewModel.dni}, ha solicitado su inscripción como socio en el Club Deportivo."

        tvParrafo2?.text = "El valor de la cuota mensual asciende a $montoStr.\nEl socio se compromete a pagar el monto de la cuota los días $diaMes de cada mes."

        tvParrafo3?.text = "Si a la fecha de vencimiento no ha cancelado la cuota, quedará inactivo y no podrá acceder a ninguna actividad hasta pagar la cuota correspondiente."

        tvParrafo4?.text = "El socio declara conocer y aceptar el reglamento interno del club."

        // Botón IMPRIMIR (Avanza a mensaje de aceptación del contrato)
        view.findViewById<Button>(R.id.btnImprimirContrato)?.setOnClickListener {
            val intent = Intent(requireContext(), RegistroClienteMensajeAceptacionContrato::class.java)
            startActivity(intent)
        }
    }
}
