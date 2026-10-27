package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.ConexionMySql
import com.grupo3.clubdeportivo.clases.Modelos.Rol
import com.grupo3.clubdeportivo.clases.Modelos.Usuario

class LoginRepo(private val conexionDatabase: ConexionMySql) {

    fun loguearUsuario(username: String, password: String): Usuario? {
        val conn = conexionDatabase.getMySqlConnection() ?: return null
        var usuario: Usuario? = null

        try {
            val stmt = conn.prepareCall("{call LoginUsuario(?, ?)}")
            stmt.setString(1, username)
            stmt.setString(2, password)
            val reader = stmt.executeQuery()

            if (reader.next()) {
                val rolObj = Rol().apply {
                    nombre = reader.getString("rol") ?: ""
                }
                usuario = Usuario().apply {
                    id = reader.getInt("id")
                    this.username = reader.getString("username") ?: ""
                    activo = reader.getBoolean("activo")
                    nombre = reader.getString("nombre") ?: ""
                    apellido = reader.getString("apellido") ?: ""
                    rol = rolObj
                }
            }
            reader.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return usuario
    }
}
