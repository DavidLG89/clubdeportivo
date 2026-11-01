package com.grupo3.clubdeportivo.clases.BaseDatos

import android.content.Context

object DbHelperObject {
    @Volatile private var instance: DbHelper? = null

    fun get(context: Context): DbHelper =
        instance ?: synchronized(this) {
            instance ?: DbHelper(context.applicationContext).also { instance = it }
        }
}