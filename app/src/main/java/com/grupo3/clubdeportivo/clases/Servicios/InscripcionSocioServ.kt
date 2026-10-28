package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Cuota
import com.grupo3.clubdeportivo.clases.Repositorios.InscripcionRepo

data class ResultadoInscripcion(
    val ok: Boolean,
    val mensaje: String,
    val idInscripcion: Int
)

class InscripcionSocioServ(private val repo: InscripcionRepo) {

    fun formalizarSocio(cliente: Cliente, cuota: Cuota): ResultadoInscripcion {
        return try {
            if (cliente.idCliente <= 0 || cuota.montoCuota <= 0) {
                return ResultadoInscripcion(false, "Cliente o cuota no válidas", 0)
            }

            val idInscripcion = repo.formalizarInscripcion(cliente.idCliente, cuota.montoCuota)

            if (idInscripcion <= 0) {
                return ResultadoInscripcion(false, "Error al realizar la inscripción del socio", 0)
            }

            ResultadoInscripcion(true, "Cliente inscrito como socio de manera exitosa", idInscripcion)
        } catch (ex: Exception) {
            throw Exception("Error service inscripción: " + ex.message, ex)
        }
    }
}
