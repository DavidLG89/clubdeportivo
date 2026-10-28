package com.grupo3.clubdeportivo.clases.Repositorios

import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelper
import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite
import com.grupo3.clubdeportivo.clases.Modelos.Rol
import com.grupo3.clubdeportivo.clases.Modelos.Usuario
import com.grupo3.clubdeportivo.clases.base_datos_proced.ProcedimientosAlmacenados

class LoginRepo(private val dbHelper: DbHelper) {

    fun loguearUsuario(username: String, password: String): Usuario? {
        val sql = """
            SELECT u.id, u.username, u.activo, r.nombre AS rol, p.nombre, p.apellido
            FROM usuarios u
            INNER JOIN personas p ON u.persona_id = p.id
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE LOWER(TRIM(u.username)) = LOWER(?) AND TRIM(u.password) = ? AND u.activo = 1
        """.trimIndent()

        dbHelper.readableDatabase.rawQuery(sql, arrayOf(username.trim(), password.trim())).use { c ->
            if (!c.moveToFirst()) return null

            return Usuario().apply {
                id = c.getInt(c.getColumnIndexOrThrow("id"))
                this.username = c.getString(c.getColumnIndexOrThrow("username")) ?: ""
                activo = c.getInt(c.getColumnIndexOrThrow("activo")) == 1
                nombre = c.getString(c.getColumnIndexOrThrow("nombre")) ?: ""
                apellido = c.getString(c.getColumnIndexOrThrow("apellido")) ?: ""
                rol = Rol().apply {
                    nombre = c.getString(c.getColumnIndexOrThrow("rol")) ?: ""
                }
            }
        }
    }
}
