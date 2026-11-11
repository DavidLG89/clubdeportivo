package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
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

    fun buscarReservasPorDni(dni: String) : List<ReservaDTO>? {
        return repo.buscarReservasPorDni(dni)
    }
    fun buscarReservaPorId(idReserva: Int): ReservaDTO? {
        return repo.buscarReservaPorId(idReserva)
    }

    fun buscarReservaPendientePagoPorId(idReserva: Int): ResultadoReserva {
        val reserva = repo.buscarReservaPorId(idReserva)
            ?: return ResultadoReserva(false, "Reserva no existe", null)

        if (reserva.estadoReserva != "Pendiente de pago") {
            return ResultadoReserva(false, "Reserva se encuentra pagada", null)
        }

        if (esAnteriorAHoy(reserva.fechaHoraReserva)) {
            return ResultadoReserva(false, "Reserva no vigente", null)
        }

        return ResultadoReserva(true, "Reserva obtenida exitosamente", reserva)
    }

   fun generarReserva(
        actividad: Actividad,
        cliente: Cliente,
        fecha: Date
    ): ResultadoReserva {
        if (cliente is Socio && !cliente.estaActivo()) {
            return ResultadoReserva(false, "El socio no está activo. Debe regularizar sus cuotas.", null)
        }

        if (!cliente.tieneAptoFisico()) {
            return ResultadoReserva(false, "El cliente no tiene apto físico. No puede reservar.", null)
        }

        val reservasCliente = repo.buscarReservasPorDni(cliente.dni)

        val reservasMismoHorario = reservasCliente.filter {
            FechaDb.mismoMinuto(it.fechaHoraReserva, fecha)
        }

        if (reservasMismoHorario.any { it.idActividad == actividad.id }) {
            return ResultadoReserva(false, "Ya tiene una reserva para esta actividad en ese horario.", null)
        }

        if (reservasMismoHorario.isNotEmpty()) {
            return ResultadoReserva(false, "Tiene otra actividad reservada en ese mismo horario.", null)
        }

        val idReserva = repo.generarReservaRepo(actividad, cliente, fecha)

        if (idReserva <= 0) {
            val mensaje = when (idReserva) {
                ReservaRepo.CodigoReserva.SIN_PROGRAMACION -> "No existe una clase programada para ese horario."
                ReservaRepo.CodigoReserva.SIN_CUPOS -> "No hay cupos disponibles para ese horario."
                ReservaRepo.CodigoReserva.YA_RESERVADA -> "Ya tiene una reserva para esta actividad en ese horario."
                else -> "No se pudo registrar la reserva. Intente nuevamente; si el problema continúa, contacte al administrador."
            }
            return ResultadoReserva(false, mensaje, null)
        }

        val reserva = buscarReservaPorId(idReserva)
            ?: return ResultadoReserva(
                false,
                "La reserva fue registrada (N° $idReserva) pero no se pudo mostrar. Consúltela en el listado de reservas.",
                null
            )

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
