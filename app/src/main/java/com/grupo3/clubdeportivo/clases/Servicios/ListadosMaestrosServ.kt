package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Actividad
import com.grupo3.clubdeportivo.clases.Modelos.MetodoPago
import com.grupo3.clubdeportivo.clases.Repositorios.ActividadRepo
import com.grupo3.clubdeportivo.clases.Repositorios.MetodoPagoRepo

class ListadosMaestrosServ {
    private var mPagoRepo: MetodoPagoRepo? = null
    private var actRepo: ActividadRepo? = null

    constructor(actRepo: ActividadRepo) {
        this.actRepo = actRepo
    }

    constructor(mPagoRepo: MetodoPagoRepo) {
        this.mPagoRepo = mPagoRepo
    }

    constructor(mPagoRepo: MetodoPagoRepo, actRepo: ActividadRepo) {
        this.mPagoRepo = mPagoRepo
        this.actRepo = actRepo
    }

    fun obtenerMetodosPago(): List<MetodoPago> {
        return mPagoRepo?.obtenerLista() ?: emptyList()
    }

    fun obtenerActividades(): List<Actividad> {
        return actRepo?.obtenerLista() ?: emptyList()
    }
}
