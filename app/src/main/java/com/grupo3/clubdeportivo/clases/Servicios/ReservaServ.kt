package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.clases.Repositorios.ReservaRepo
import java.util.Calendar
import java.util.Date

data class ResultadoReserva(
    val ok: Boolean,
    val mensaje: String,
    val reserva: ReservaDTO?
)

class ReservaServ(private val repo: ReservaRepo) {

    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {
        return repo.buscarReservaPorId(idReserva)
    }

    fun buscarReservaPendientePagoPorId(idReserva: Int): ResultadoReserva {
        val reserva = repo.buscarReservaPorId(idReserva)
            ?: return ResultadoReserva(false, "Reserva no existe", null)

        if (reserva.estadoReserva != "Pendiente de pago") {
            return ResultadoReserva(false, "Reserva se encuentra pagada", null)
        }

        if (esAnteriorAHoy(reserva.fechaHora)) {
            return ResultadoReserva(false, "Reserva no vigente", null)
        }

        return ResultadoReserva(true, "Reserva obtenida exitosamente", reserva)
    }

    fun buscarReservaExistentePorDniYActividad(dni: String, idActividad: Int): ResultadoReserva {
        val reserva = repo.buscarReservaPorDniYActividadRepo(dni, idActividad)
            ?: return ResultadoReserva(false, "Reserva no existe", null)

        return ResultadoReserva(true, "Reserva obtenida exitosamente", reserva)
    }

    fun buscarReservaPendientePorDniYActividad(dni: String, idActividad: Int): ResultadoReserva {
        val reserva = repo.buscarReservaPorDniYActividadRepo(dni, idActividad)
            ?: return ResultadoReserva(false, "Reserva no existe", null)

        if (reserva.estadoReserva != "Pendiente de pago") {
            return ResultadoReserva(false, "Reserva se encuentra pagada", null)
        }

        if (esAnteriorAHoy(reserva.fechaHora)) {
            return ResultadoReserva(false, "Reserva no vigente", null)
        }

        return ResultadoReserva(true, "Reserva obtenida exitosamente", reserva)
    }

    fun generarReserva(
        actividad: Actividad,
        cliente: Cliente,
        fecha: String
    ): ResultadoReserva {
        if (cliente is Socio && !cliente.estaActivo()) {
            return ResultadoReserva(false, "El socio no está activo. Debe regularizar sus cuotas.", null)
        }

        if (!cliente.tieneAptoFisico()) {
            return ResultadoReserva(false, "El cliente no tiene apto físico. No puede reservar.", null)
        }

        if (fecha == "") {
            return ResultadoReserva(false, "No se encontró programación.", null)
        }

        if (fecha == "") {
            return ResultadoReserva(false, "Actividad no está vigente.", null)
        }

        if (fecha == "") {
            return ResultadoReserva(false, "Actividad no tiene cupos disponibles.", null)
        }

        val reservaBuscada = buscarReservaExistentePorDniYActividad(cliente.dni, actividad.id)

        if (reservaBuscada.reserva != null) {
            return ResultadoReserva(false, "Reserva ya fue realizada por el cliente", null)
        }

        val idReserva = repo.generarReservaRepo(actividad, cliente, fecha)

        if (idReserva <= 0) {
            return ResultadoReserva(false, "Error inesperado al crear la reserva.", null)
        }

        val reserva = buscarReservaPorId(idReserva)
            ?: return ResultadoReserva(false, "Error al intentar obtener la reserva", null)

        return if (cliente is Socio) {

            ResultadoReserva(true, "Reserva autorizada. El socio no debe abonar el servicio.", reserva)
        } else {
            ResultadoReserva(true, "Reserva creada. El cliente debe abonar el servicio.", reserva)
        }
    }

    private fun esAnteriorAHoy(fecha: Date): Boolean {
        val calFecha = Calendar.getInstance().apply { time = fecha }
        val calHoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calFecha.before(calHoy)
    }
}
