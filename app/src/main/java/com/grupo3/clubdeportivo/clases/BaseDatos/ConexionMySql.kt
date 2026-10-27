package com.grupo3.clubdeportivo.clases.BaseDatos

import java.sql.Connection
import java.sql.DriverManager

class ConexionMySql : Conexion() {
    private var conn: Connection? = null
    private var cadenaConexion: String = ""

    fun getMySqlConnection(): Connection? {
        cadenaConexion = "jdbc:mysql://$server/$database?user=$username&password=$password"
        return try {
            Class.forName("com.mysql.jdbc.Driver")
            conn = DriverManager.getConnection(cadenaConexion)
            conn
        } catch (ex: Exception) {
            null
        }
    }
}
