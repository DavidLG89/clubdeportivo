package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Usuario
import com.grupo3.clubdeportivo.clases.Repositorios.LoginRepo

class LoginServ(private val repository: LoginRepo) {

    fun login(username: String?, password: String?): Usuario? {
        val cleanUser = username?.trim()
        val cleanPass = password?.trim()

        if (cleanUser.isNullOrEmpty() || cleanPass.isNullOrEmpty()) {
            return null
        }

        val usuario = repository.loguearUsuario(cleanUser, cleanPass) ?: return null

        if (!usuario.rol?.nombre.equals("admin", ignoreCase = true)) {
            return null
        }

        return usuario
    }
}
