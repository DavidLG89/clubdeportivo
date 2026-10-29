package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Usuario
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class LoginRepo(private val conexionDatabase: conexionBDsqlite) {

    fun loguearUsuario(username: String, password: String): Usuario? {
        val conn = conexionDatabase.getConexionBDsqlite() ?: return null
        return ProcedimientosAlmacenados.loginUsuario(conn, username, password)
    }
}
