package com.grupo3.clubdeportivo.ui.carnet

import android.content.Context
import androidx.lifecycle.ViewModel
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Modelos.Carnet
import com.grupo3.clubdeportivo.clases.Repositorios.CarnetRepo
import com.grupo3.clubdeportivo.clases.Repositorios.ClienteRepo
import com.grupo3.clubdeportivo.clases.Servicios.CarnetServ

sealed class ResultadoBusquedaCarnet {
    object SocioNoExiste : ResultadoBusquedaCarnet()
    data class Exito(val carnet: Carnet) : ResultadoBusquedaCarnet()
    data class Error(val mensaje: String) : ResultadoBusquedaCarnet()
}

class CarnetViewModel : ViewModel() {
    var dni: String = ""
    var nombre: String = ""
    var numeroSocio: String = ""
    var estadoSocio: String = ""
    var aptoFisico: String = ""

    fun buscarYProcesarCarnet(dniIngresado: String, context: Context): ResultadoBusquedaCarnet {
        val dbHelper = DbHelperObject.get(context)
        val clienteRepo = ClienteRepo(dbHelper)
        val carnetRepo = CarnetRepo(dbHelper)
        val carnetServ = CarnetServ(clienteRepo, carnetRepo)

        val socio = carnetServ.buscarSocio(dniIngresado)
            ?: return ResultadoBusquedaCarnet.SocioNoExiste

        val carnet = carnetServ.obtenerOActualizarCarnet(socio)
            ?: return ResultadoBusquedaCarnet.Error("Error al procesar el carnet")

        this.dni = socio.dni
        this.nombre = "${socio.nombre} ${socio.apellido}"
        this.numeroSocio = carnet.id.toString()
        this.estadoSocio = if (carnet.estado) "Activo" else "Inactivo"
        this.aptoFisico = if (socio.aptoFisico) "Si" else "No"

        return ResultadoBusquedaCarnet.Exito(carnet)
    }
}
