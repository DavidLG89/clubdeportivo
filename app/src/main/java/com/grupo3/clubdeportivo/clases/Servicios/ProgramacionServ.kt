package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Programacion
import com.grupo3.clubdeportivo.clases.Repositorios.ProgramacionRepo
import java.util.Date

class ProgramacionServ(private val progRepo: ProgramacionRepo) {

    fun obtenerProgramacionPorActividad(idActividad: Int, fecha: String): List<Programacion> {
        return progRepo.buscarProgramacionPorActividad(idActividad, fecha)
    }
}
