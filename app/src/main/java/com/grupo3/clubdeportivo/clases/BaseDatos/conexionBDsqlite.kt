package com.grupo3.clubdeportivo.clases.BaseDatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class conexionBDsqlite : Conexion() {
    private var db: SQLiteDatabase? = null
    private var appContext: Context? = null

    init {
        database = "Club_deportivo_BaseDatosSqlite"
    }

    fun setContext(ctx: Context) {
        this.appContext = ctx.applicationContext
    }

    fun getConexionBDsqlite(ctx: Context? = null): SQLiteDatabase? {
        if (ctx != null) {
            this.appContext = ctx.applicationContext
        }

        if (db != null && db!!.isOpen) {
            return db
        }

        val context = appContext ?: return null
        val dbName = if (database.endsWith(".db")) database else "$database.db"
        val helper = DBHelper(context, dbName)
        db = helper.writableDatabase
        return db
    }

    private class DBHelper(context: Context, name: String) :
        SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) {}
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
}
