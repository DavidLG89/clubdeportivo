package com.grupo3.clubdeportivo.clases.Documentos

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object GeneradorContrato {

    fun generarTextoContrato(
        nombre: String,
        apellido: String,
        dni: String,
        montoCuota: Double,
        diaPago: Int
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaHoy = sdf.format(Date())
        val anioActual = Calendar.getInstance().get(Calendar.YEAR)

        return """
            =================================================
                          CONTRATO DE SOCIO
            =================================================
            Fecha: $fechaHoy

            Se deja constancia que el cliente $nombre $apellido, DNI $dni,
            ha solicitado su inscripción como socio en el Club Deportivo.

            El valor de la cuota mensual asciende a $$montoCuota.

            El socio se compromete a pagar el monto de la cuota los días $diaPago de cada mes.

            Si a la fecha de vencimiento no ha cancelado la cuota, quedará inactivo
            y no podrá acceder a ninguna actividad hasta pagar la cuota correspondiente.

            El socio declara conocer y aceptar el reglamento interno del club.

            _____________________________________
            Firma del Socio

            Club Deportivo - $anioActual
            =================================================
        """.trimIndent()
    }
}
