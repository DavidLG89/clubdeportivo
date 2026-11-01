package com.grupo3.clubdeportivo.clases.Documentos

import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.Socio

object GeneradorCarnet {

    fun generarDatosCarnet(socio: Cliente): String {
        val estaActivo = socio is Socio && socio.estado
        val estadoTexto = if (estaActivo) "SOCIO ACTIVO" else "SOCIO INACTIVO"

        return """
            =========================================
                        CARNET DE SOCIO
               CLUB DEPORTIVO ESTRELLA DEL SUR
            =========================================
            Nombre: ${socio.nombre.uppercase()} ${socio.apellido.uppercase()}
            DNI: ${socio.dni}
            Estado: $estadoTexto
            =========================================
        """.trimIndent()
    }
}
