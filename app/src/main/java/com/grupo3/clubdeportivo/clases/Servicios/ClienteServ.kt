package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo

data class ResultadoCliente(
    val ok: Boolean,
    val mensaje: String,
    val cliente: Cliente?
)

class ClienteServ(private val repo: ClienteRepo) {

    fun realizarRegistro(cliente: Cliente?, esSocio: Boolean): ResultadoCliente {
        if (cliente == null) {
            return ResultadoCliente(false, "No se puede realizar registro sin información", null)
        }

        val idRegistrado = repo.registrarClienteRepo(
            cliente.nombre,
            cliente.apellido,
            cliente.dni,
            cliente.aptoFisico,
            esSocio
        )

        if (idRegistrado <= 0) {
            return ResultadoCliente(false, "Error al realizar el registro", null)
        }

        cliente.idCliente = idRegistrado

        if (esSocio && cliente is Socio) {
            cliente.idSocio = idRegistrado
        }

        return ResultadoCliente(true, "Cliente registrado con éxito", cliente)
    }

    fun buscarClientePorDni(dni: String): Cliente? {
        return repo.buscarClientePorDniRepo(dni)
    }

    fun actualizarAptoFisico(cliente: Cliente): Boolean {
        return repo.actualizarAptoFisico(cliente)
    }

    fun asignarANoSocio(cliente: Cliente): Int {
        return repo.asignarANoSocio(cliente)
    }
}
