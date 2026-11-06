package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Carnet
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.clases.Repositorios.CarnetRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo

class CarnetServ(
    private val clienteRepo: ClienteRepo,
    private val carnetRepo: CarnetRepo
) {
    fun buscarSocio(dni: String): Socio? {
        val cliente = clienteRepo.buscarClientePorDniRepo(dni)
        return if (cliente is Socio) cliente else null
    }

    fun obtenerOActualizarCarnet(socio: Socio): Carnet? {
        return carnetRepo.procesarCarnetSocio(socio)
    }
}
