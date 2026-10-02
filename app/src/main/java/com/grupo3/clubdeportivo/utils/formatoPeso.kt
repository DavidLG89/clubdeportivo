package com.grupo3.clubdeportivo.utils

import java.text.NumberFormat
import java.util.Locale

fun Int.formatoPeso() : String =
    "$" + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-AR")).format(this)