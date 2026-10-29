package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Interfaces.IListado
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago

class MetodoPagoRepo(@Suppress("UNUSED_PARAMETER") conexionBDsqlite: conexionBDsqlite) : IListado<MetodoPago> {

    override fun obtenerLista(): List<MetodoPago> {
        return emptyList()
    }
}
