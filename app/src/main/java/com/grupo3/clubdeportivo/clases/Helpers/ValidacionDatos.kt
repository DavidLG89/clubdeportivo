package com.grupo3.clubdeportivo.clases.Helpers

object ValidacionDatos {

    fun soloLetras(texto: String): Boolean {
        return texto.all { it.isLetter() || it.isWhitespace() }
    }

    data class ResultadoValidacion(val esValido: Boolean, val mensaje: String)

    fun soloNumeros(texto: String): ResultadoValidacion {
        return if (texto.toIntOrNull() == null) {
            ResultadoValidacion(false, "Debe ingresar solo números")
        } else {
            ResultadoValidacion(true, "")
        }
    }

    fun validarDni(dni: String?): ResultadoValidacion {
        if (dni.isNullOrEmpty()) {
            return ResultadoValidacion(false, "Debe ingresar un DNI")
        }
        if (!dni.all { it.isDigit() }) {
            return ResultadoValidacion(false, "DNI debe contener solo números")
        }
        if (dni.length != 8) {
            return ResultadoValidacion(false, "DNI solo puede contener 8 dígitos")
        }
        return ResultadoValidacion(true, "")
    }

    fun validarMonto(monto: String?): ResultadoValidacion {
        if (monto.isNullOrEmpty()) {
            return ResultadoValidacion(false, "Debe ingresar un monto")
        }
        if (monto.toDoubleOrNull() == null) {
            return ResultadoValidacion(false, "Debe ingresar un monto válido")
        }
        return ResultadoValidacion(true, "")
    }
}
