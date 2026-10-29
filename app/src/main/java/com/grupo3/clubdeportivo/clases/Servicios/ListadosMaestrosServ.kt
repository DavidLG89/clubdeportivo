package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.ConceptoPago
import com.grupo3.clubdeportivo.clases.Repositorios.ActividadRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ConceptoPagoRepo

class ListadosMaestrosServ {
    private var cPagorepo: ConceptoPagoRepo? = null
    private var actRepo: ActividadRepo? = null

    constructor(actRepo: ActividadRepo) {
        this.actRepo = actRepo
    }

    constructor(cPagoRepo: ConceptoPagoRepo) {
        this.cPagorepo = cPagoRepo
    }

    constructor(cPagoRepo: ConceptoPagoRepo, actRepo: ActividadRepo) {
        this.cPagorepo = cPagoRepo
        this.actRepo = actRepo
    }

    fun obtenerConceptosPago(): List<ConceptoPago> {
        return cPagorepo?.obtenerLista() ?: emptyList()
    }

    fun obtenerActividades(): List<Actividad> {
        return actRepo?.obtenerLista() ?: emptyList()
    }
}
