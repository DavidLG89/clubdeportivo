package com.grupo3.clubdeportivo.clases.Helpers

object ValidacionDatos {

    fun soloLetras(texto: String): Boolean {
        return texto.all { it.isLetter() || it.isWhitespace() }
    }

    data class ResultadoValidacion(val esValido: Boolean, val mensaje: String)

    fun validarDni(dni: String?): ResultadoValidacion {
        if (dni.isNullOrEmpty()) {
            return ResultadoValidacion(false, "Debe ingresar un DNI")
        }
        if (!dni.all { it in '0'..'9' }) {
            return ResultadoValidacion(false, "El DNI debe contener solo números")
        }
        if (dni.length != 8) {
            return ResultadoValidacion(false, "El DNI debe tener 8 dígitos")
        }
        return ResultadoValidacion(true, "")
    }

    fun validarMonto(monto: String?): ResultadoValidacion {
        if (monto.isNullOrBlank()) {
            return ResultadoValidacion(false, "Debe ingresar un monto")
        }
        val valor = monto.toDoubleOrNull()
        if (valor == null || valor.isNaN() || valor.isInfinite()) {
            return ResultadoValidacion(false, "Debe ingresar un monto válido")
        }
        if(valor <= 0) {
            return ResultadoValidacion(false, "Monto debe ser mayor a 0")
        }
        return ResultadoValidacion(true, "")
    }
}
