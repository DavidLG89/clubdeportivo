package com.grupo3.clubdeportivo.clases.DTOS

import com.grupo3.clubdeportivo.clases.Modelos.Socio
import java.util.Date

data class CuotaDTO(
    var idCuota: Int = 0,
    //var idSocio: Int = 0,
    //var nombre: String = "",
    //var apellido: String = "",
    //var dni: String = "",
    //var email: String = "",
    var socio: Socio? = null,
    var estadoSocio: Boolean = false,
    var montoCuota: Double = 0.0,
    var fechaVencimiento: Date = Date(),
    var estadoCuota: String = ""
)
