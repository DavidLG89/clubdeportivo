package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.ConceptoPago
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.clases.Repositorios.ActividadRepo
import com.grupo3.clubdeportivo.clases.Repositorios.MetodoPagoRepo

class ListadosMaestrosServ {
    private var cPagorepo: ConceptoPagoRepo? = null
    private var mPagoRepo: MetodoPagoRepo? = null
    private var actRepo: ActividadRepo? = null

    constructor(actRepo: ActividadRepo) {
        this.actRepo = actRepo
    }

    constructor(cPagoRepo: ConceptoPagoRepo, mPagoRepo: MetodoPagoRepo) {
        this.cPagorepo = cPagoRepo
        this.mPagoRepo = mPagoRepo
    }

    constructor(cPagoRepo: ConceptoPagoRepo, mPagoRepo: MetodoPagoRepo, actRepo: ActividadRepo) {
        this.cPagorepo = cPagoRepo
        this.mPagoRepo = mPagoRepo
        this.actRepo = actRepo
    }

    fun obtenerMetodosPago(): List<MetodoPago> {
        return mPagoRepo?.obtenerLista() ?: emptyList()
    }

    fun obtenerConceptosPago(): List<ConceptoPago> {
        return cPagorepo?.obtenerLista() ?: emptyList()
    }

    fun obtenerActividades(): List<Actividad> {
        return actRepo?.obtenerLista() ?: emptyList()
    }
}
