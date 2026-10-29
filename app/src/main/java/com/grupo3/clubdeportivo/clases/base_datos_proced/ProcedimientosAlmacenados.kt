package com.grupo3.clubdeportivo.clases.base_datos_proced

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.DTOS.ReservaDTO
import com.grupo3.clubdeportivo.clases.Modelos.Cliente
import com.grupo3.clubdeportivo.clases.Modelos.NoSocio
import com.grupo3.clubdeportivo.clases.Modelos.Rol
import com.grupo3.clubdeportivo.clases.Modelos.Socio
import com.grupo3.clubdeportivo.clases.Modelos.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ProcedimientosAlmacenados {

    fun loginUsuario(db: SQLiteDatabase, username: String, password: String): Usuario? {
        val cleanUser = username.trim()
        val cleanPass = password.trim()
        val query = """
            SELECT u.id, u.username, u.activo, r.id AS rol_id, r.nombre AS rol, p.nombre, p.apellido
            FROM usuarios u
            INNER JOIN personas p ON u.persona_id = p.id
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE LOWER(TRIM(u.username)) = LOWER(?) AND TRIM(u.password) = ? AND u.activo = 1
        """.trimIndent()

        return try {
            val cursor = db.rawQuery(query, arrayOf(cleanUser, cleanPass))
            var usuario: Usuario? = null

            if (cursor.moveToFirst()) {
                val rolObj = Rol().apply {
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("rol")) ?: ""
                }
                usuario = Usuario().apply {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    this.username = cursor.getString(cursor.getColumnIndexOrThrow("username")) ?: ""
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    rol = rolObj
                }
            }
            cursor.close()
            usuario
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun registrarCliente(
        db: SQLiteDatabase,
        nombre: String,
        apellido: String,
        dni: String,
        aptoFisico: Boolean,
        esSocio: Boolean
    ): Int {
        val checkCursor = db.rawQuery("SELECT id FROM personas WHERE dni = ?", arrayOf(dni))
        val existe = checkCursor.moveToFirst()
        checkCursor.close()

        if (existe) {
            return -1
        }

        db.beginTransaction()
        return try {
            val cvPersona = ContentValues().apply {
                put("nombre", nombre)
                put("apellido", apellido)
                put("dni", dni)
            }
            val personaId = db.insert("personas", null, cvPersona)
            if (personaId == -1L) throw Exception("Error al insertar persona")

            val cvCliente = ContentValues().apply {
                put("persona_id", personaId)
                put("apto_fisico", if (aptoFisico) 1 else 0)
            }
            val clienteId = db.insert("clientes", null, cvCliente)
            if (clienteId == -1L) throw Exception("Error al insertar cliente")

            if (!esSocio) {
                val cvNoSocio = ContentValues().apply {
                    put("cliente_id", clienteId)
                    put("acceso_diario", 1)
                }
                db.insert("no_socios", null, cvNoSocio)
            }

            db.setTransactionSuccessful()
            clienteId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -2
        } finally {
            db.endTransaction()
        }
    }

    fun formalizarInscripcion(db: SQLiteDatabase, clienteId: Int, montoCuota: Double): Int {
        val checkCursor = db.rawQuery("SELECT id FROM socios WHERE cliente_id = ?", arrayOf(clienteId.toString()))
        val existe = checkCursor.moveToFirst()
        checkCursor.close()

        if (existe) {
            return -1
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        db.beginTransaction()
        return try {
            val cvSocio = ContentValues().apply {
                put("cliente_id", clienteId)
                put("estado", 0)
            }
            val socioId = db.insert("socios", null, cvSocio)
            if (socioId == -1L) throw Exception("Error al insertar socio")

            val cvInsc = ContentValues().apply {
                put("socio_id", socioId)
                put("fecha_inscripcion", fechaHoy)
            }
            db.insert("inscripciones", null, cvInsc)

            val cvCuota = ContentValues().apply {
                put("socio_id", socioId)
                put("monto_cuota", montoCuota)
                put("fecha_vencimiento", fechaHoy)
                put("estado_cuota", "Pendiente")
            }
            db.insert("cuotas", null, cvCuota)

            db.setTransactionSuccessful()
            socioId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }

    fun buscarClientePorDni(db: SQLiteDatabase, dni: String): Cliente? {
        val query = """
            SELECT cl.id AS id_cliente, p.nombre, p.apellido, p.dni, cl.apto_fisico,
                   CASE 
                       WHEN s.id IS NOT NULL THEN 'Socio'
                       WHEN ns.id IS NOT NULL THEN 'NoSocio'
                       ELSE 'Cliente'
                   END AS tipo_cliente,
                   s.id AS id_socio, s.estado AS estado_socio, ns.id AS id_no_socio, ns.acceso_diario
            FROM clientes cl
            JOIN personas p ON p.id = cl.persona_id
            LEFT JOIN socios s ON s.cliente_id = cl.id
            LEFT JOIN no_socios ns ON ns.cliente_id = cl.id
            WHERE p.dni = ?
        """.trimIndent()

        return try {
            val cursor = db.rawQuery(query, arrayOf(dni))
            var cliente: Cliente? = null

            if (cursor.moveToFirst()) {
                val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                val dniVal = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                val aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("apto_fisico")) == 1
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_cliente")) ?: ""

                cliente = when (tipo) {
                    "Socio" -> {
                        val idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio"))
                        val estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                        Socio(idCliente, nombre, apellido, dniVal, aptoFisico, estado).apply {
                            this.idSocio = idSocio
                        }
                    }
                    "NoSocio" -> {
                        val idNoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id_no_socio"))
                        val accesoDiario = cursor.getInt(cursor.getColumnIndexOrThrow("acceso_diario")) == 1
                        NoSocio(idCliente, nombre, apellido, dniVal, aptoFisico).apply {
                            this.idNoSocio = idNoSocio
                            this.accesoDiario = accesoDiario
                        }
                    }
                    else -> Cliente(idCliente, nombre, apellido, dniVal, aptoFisico)
                }
            }
            cursor.close()
            cliente
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun registrarPagoCuota(
        db: SQLiteDatabase,
        socioId: Int,
        monto: Double,
        conceptoId: Int,
        metodoId: Int
    ): Int {
        val selectCursor = db.rawQuery(
            "SELECT id FROM cuotas WHERE socio_id = ? AND estado_cuota = 'Pendiente' ORDER BY fecha_vencimiento LIMIT 1",
            arrayOf(socioId.toString())
        )
        var cuotaId: Int? = null
        if (selectCursor.moveToFirst()) {
            cuotaId = selectCursor.getInt(0)
        }
        selectCursor.close()

        if (cuotaId == null) {
            return -2
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        db.beginTransaction()
        return try {
            val cvPago = ContentValues().apply {
                put("cuota_id", cuotaId)
                put("fecha_pago", fechaHoy)
                put("monto", monto)
                put("concepto_id", conceptoId)
                put("metodo_id", metodoId)
            }
            val pagoId = db.insert("pagos", null, cvPago)

            val cvCuota = ContentValues().apply {
                put("estado_cuota", "Pagada")
            }
            db.update("cuotas", cvCuota, "id = ?", arrayOf(cuotaId.toString()))

            val cvSocio = ContentValues().apply {
                put("estado", 1)
            }
            db.update("socios", cvSocio, "id = ?", arrayOf(socioId.toString()))

            db.setTransactionSuccessful()
            pagoId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }

    fun registrarPagoActividad(
        db: SQLiteDatabase,
        reservaId: Int,
        monto: Double,
        conceptoId: Int,
        metodoId: Int
    ): Int {
        val curRes = db.rawQuery("SELECT programacion_id FROM reservas WHERE id = ?", arrayOf(reservaId.toString()))
        var programacionId: Int? = null
        if (curRes.moveToFirst()) {
            programacionId = curRes.getInt(0)
        }
        curRes.close()

        if (programacionId == null) {
            return -2
        }

        val curCupo = db.rawQuery("SELECT cupos_disponibles FROM programaciones WHERE id = ?", arrayOf(programacionId.toString()))
        var cupos = 0
        if (curCupo.moveToFirst()) {
            cupos = curCupo.getInt(0)
        }
        curCupo.close()

        if (cupos <= 0) {
            return -2
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        db.beginTransaction()
        return try {
            val cvPago = ContentValues().apply {
                put("reserva_id", reservaId)
                put("fecha_pago", fechaHoy)
                put("monto", monto)
                put("concepto_id", conceptoId)
                put("metodo_id", metodoId)
            }
            val pagoId = db.insert("pagos", null, cvPago)

            val cvReserva = ContentValues().apply {
                put("estado", "Pagado")
            }
            db.update("reservas", cvReserva, "id = ?", arrayOf(reservaId.toString()))

            db.execSQL("UPDATE programaciones SET cupos_disponibles = cupos_disponibles - 1 WHERE id = ?", arrayOf(programacionId.toString()))

            db.setTransactionSuccessful()
            pagoId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerCuotasPorDni(db: SQLiteDatabase, dni: String): List<CuotaDTO> {
        val query = """
            SELECT p.nombre, p.apellido, p.dni, s.id, s.estado AS estado_socio, c.id AS id_cuota, c.monto_cuota, c.estado_cuota, c.fecha_vencimiento
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON cl.id = s.cliente_id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE p.dni = ?
        """.trimIndent()

        val list = mutableListOf<CuotaDTO>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val cursor = db.rawQuery(query, arrayOf(dni))
            while (cursor.moveToNext()) {
                val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")) ?: ""
                val fechaDate = try { sdf.parse(fechaStr) ?: Date() } catch (_: Exception) { Date() }

                val cuota = CuotaDTO().apply {
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    this.dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                    idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    estadoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("estado_socio")) == 1
                    montoCuota = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                    fechaVencimiento = fechaDate
                    estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")) ?: ""
                }
                list.add(cuota)
            }
            cursor.close()
            list
        } catch (e: Exception) {
            e.printStackTrace()
            list
        }
    }

    fun generarReserva(db: SQLiteDatabase, idActividad: Int, clienteId: Int, fechaHora: Date): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaHoraStr = sdf.format(fechaHora)

        val curProg = db.rawQuery(
            "SELECT id, cupos_disponibles FROM programaciones WHERE actividad_id = ? AND fecha_hora = ? LIMIT 1",
            arrayOf(idActividad.toString(), fechaHoraStr)
        )
        if (!curProg.moveToFirst()) {
            curProg.close()
            return -1
        }

        val programacionId = curProg.getInt(0)
        val cupos = curProg.getInt(1)
        curProg.close()

        if (cupos <= 0) {
            return -2
        }

        val curCheck = db.rawQuery(
            "SELECT 1 FROM reservas WHERE programacion_id = ? AND cliente_id = ?",
            arrayOf(programacionId.toString(), clienteId.toString())
        )
        val yaExiste = curCheck.moveToFirst()
        curCheck.close()

        if (yaExiste) {
            return -3
        }

        val curSocio = db.rawQuery(
            "SELECT id FROM socios WHERE cliente_id = ? AND estado = 1",
            arrayOf(clienteId.toString())
        )
        val esSocio = curSocio.moveToFirst()
        curSocio.close()

        val fechaReserva = sdf.format(Date())

        db.beginTransaction()
        return try {
            val estadoReserva = if (esSocio) "Autorizada" else "Pendiente de pago"
            val cvReserva = ContentValues().apply {
                put("programacion_id", programacionId)
                put("cliente_id", clienteId)
                put("fecha_reserva", fechaReserva)
                put("estado", estadoReserva)
            }
            val reservaId = db.insert("reservas", null, cvReserva)

            if (esSocio) {
                db.execSQL("UPDATE programaciones SET cupos_disponibles = cupos_disponibles - 1 WHERE id = ?", arrayOf(programacionId.toString()))
            }

            db.setTransactionSuccessful()
            reservaId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -99
        } finally {
            db.endTransaction()
        }
    }

    fun listarVencimientos(db: SQLiteDatabase, fecha: Date): List<CuotaDTO> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaStr = sdf.format(fecha)

        val query = """
            SELECT p.nombre, p.apellido, p.dni, c.monto_cuota, c.fecha_vencimiento, c.estado_cuota
            FROM personas p
            INNER JOIN clientes cl ON p.id = cl.persona_id
            INNER JOIN socios s ON s.cliente_id = cl.id
            INNER JOIN cuotas c ON s.id = c.socio_id
            WHERE date(c.fecha_vencimiento) = date(?)
        """.trimIndent()

        val list = mutableListOf<CuotaDTO>()
        return try {
            val cursor = db.rawQuery(query, arrayOf(fechaStr))
            while (cursor.moveToNext()) {
                val fvStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")) ?: ""
                val fvDate = try { sdf.parse(fvStr) ?: Date() } catch (_: Exception) { Date() }

                val cuota = CuotaDTO().apply {
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                    montoCuota = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_cuota"))
                    fechaVencimiento = fvDate
                    estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")) ?: ""
                }
                list.add(cuota)
            }
            cursor.close()
            list
        } catch (e: Exception) {
            e.printStackTrace()
            list
        }
    }

    fun buscarReservaPorId(db: SQLiteDatabase, idReserva: Int): ReservaDTO? {
        val query = """
            SELECT r.id AS id_reserva, r.estado, r.cliente_id AS id_cliente, p.nombre, p.apellido, p.dni, a.nombre AS actividad, a.precio, pr.fecha_hora
            FROM reservas r
            INNER JOIN programaciones pr ON r.programacion_id = pr.id
            INNER JOIN actividades a ON pr.actividad_id = a.id
            INNER JOIN clientes cl ON r.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE r.id = ?
        """.trimIndent()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            val cursor = db.rawQuery(query, arrayOf(idReserva.toString()))
            var reserva: ReservaDTO? = null

            if (cursor.moveToFirst()) {
                val fhStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                val fhDate = try { sdf.parse(fhStr) ?: Date() } catch (_: Exception) { Date() }

                reserva = ReservaDTO().apply {
                    this.idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                    idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    apellidoCliente = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                    actividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    fechaHora = fhDate
                    estadoReserva = cursor.getString(cursor.getColumnIndexOrThrow("estado")) ?: ""
                }
            }
            cursor.close()
            reserva
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun buscarReservaPorDniYActividad(db: SQLiteDatabase, dni: String, idActividad: Int): ReservaDTO? {
        val query = """
            SELECT r.id AS id_reserva, cl.id AS id_cliente, p.nombre, p.apellido, p.dni, a.id AS id_actividad, a.nombre AS actividad, a.precio, r.estado, pr.fecha_hora
            FROM reservas r
            INNER JOIN programaciones pr ON r.programacion_id = pr.id
            INNER JOIN actividades a ON pr.actividad_id = a.id
            INNER JOIN clientes cl ON r.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni = ? AND a.id = ?
            ORDER BY pr.fecha_hora
        """.trimIndent()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            val cursor = db.rawQuery(query, arrayOf(dni, idActividad.toString()))
            var reserva: ReservaDTO? = null

            if (cursor.moveToFirst()) {
                val fhStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")) ?: ""
                val fhDate = try { sdf.parse(fhStr) ?: Date() } catch (_: Exception) { Date() }

                reserva = ReservaDTO().apply {
                    this.idReserva = cursor.getInt(cursor.getColumnIndexOrThrow("id_reserva"))
                    idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre")) ?: ""
                    apellidoCliente = cursor.getString(cursor.getColumnIndexOrThrow("apellido")) ?: ""
                    this.dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")) ?: ""
                    this.idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad"))
                    actividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad")) ?: ""
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    fechaHora = fhDate
                    estadoReserva = cursor.getString(cursor.getColumnIndexOrThrow("estado")) ?: ""
                }
            }
            cursor.close()
            reserva
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
