package com.grupo3.clubdeportivo.clases.Documentos

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GeneradorComprobantes {

    fun mostrarComprobanteReserva(reserva: ReservaDTO?): String {
        if (reserva == null) {
            return "Reserva no existe, no se puede crear comprobante."
        }

        val datoPago = if (reserva.estadoReserva == "Autorizada") {
            "Monto a Pagar: $ 0\n"
        } else {
            "Monto a Pagar: $ ${reserva.precio}\n"
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return """
            Id Reserva: ${reserva.idReserva}
            Nombre: ${reserva.nombreCliente}
            Apellido: ${reserva.apellidoCliente}
            DNI: ${reserva.dni}
            Actividad: ${reserva.actividad}
            Fecha y Hora: ${sdf.format(reserva.fechaHora)}
            $datoPago
        """.trimIndent()
    }

    fun mostrarComprobantePagoActividad(reserva: ReservaDTO, metodoPago: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return """
            Id Reserva: ${reserva.idReserva}
            Nombre: ${reserva.nombreCliente}
            Apellido: ${reserva.apellidoCliente}
            DNI: ${reserva.dni}
            Actividad: ${reserva.actividad}
            Fecha y Hora: ${sdf.format(reserva.fechaHora)}
            Monto Pagado: $ ${reserva.precio}
            Método Pago: $metodoPago
            Fecha Pago: ${sdf.format(Date())}
        """.trimIndent()
    }

    fun mostrarComprobantePagoCuota(cuota: CuotaDTO, metodoPago: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return """
            Nombre: ${cuota.socio?.nombre}
            Apellido: ${cuota.socio?.apellido}
            DNI: ${cuota.socio?.dni}
            Monto Pagado: $ ${cuota.montoCuota}
            Método Pago: $metodoPago
            Fecha Pago: ${sdf.format(Date())}
        """.trimIndent()
    }
}
