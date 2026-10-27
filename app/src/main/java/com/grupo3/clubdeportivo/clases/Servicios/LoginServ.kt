package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.Modelos.Usuario
import com.grupo3.clubdeportivo.clases.Repositorios.LoginRepo

class LoginServ(private val repository: LoginRepo) {

    fun login(username: String?, password: String?): Usuario? {
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            return null
        }

        val usuario = repository.loguearUsuario(username, password) ?: return null

        if (usuario.rol?.nombre != "admin") {
            return null
        }

        return usuario
    }
}
