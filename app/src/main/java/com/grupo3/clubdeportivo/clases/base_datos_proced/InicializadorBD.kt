package com.grupo3.clubdeportivo.clases.base_datos_proced

import android.content.Context
import com.grupo3.clubdeportivo.clases.BaseDatos.conexionBDsqlite

object InicializadorBD {

    @Synchronized
    fun inicializarSiNoExiste(conexionBD: conexionBDsqlite, context: Context) {
        conexionBD.setContext(context)
        val db = conexionBD.getConexionBDsqlite(context) ?: return

        db.beginTransaction()
        try {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS roles(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS personas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellido TEXT NOT NULL,
                    dni TEXT NOT NULL UNIQUE
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS clientes(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL UNIQUE,
                    apto_fisico INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY(persona_id) REFERENCES personas(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS usuarios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL UNIQUE,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    rol_id INTEGER NOT NULL,
                    activo INTEGER DEFAULT 1,
                    FOREIGN KEY(persona_id) REFERENCES personas(id),
                    FOREIGN KEY(rol_id) REFERENCES roles(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS socios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL UNIQUE,
                    estado INTEGER DEFAULT 0,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS no_socios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL UNIQUE,
                    acceso_diario INTEGER DEFAULT 0,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS inscripciones(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    socio_id INTEGER NOT NULL,
                    fecha_inscripcion TEXT NOT NULL,
                    FOREIGN KEY(socio_id) REFERENCES socios(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS carnet(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    inscripcion_id INTEGER NOT NULL UNIQUE,
                    fecha_emision TEXT NOT NULL,
                    estado INTEGER DEFAULT 1,
                    FOREIGN KEY(inscripcion_id) REFERENCES inscripciones(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS cuotas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    socio_id INTEGER NOT NULL,
                    monto_cuota REAL NOT NULL,
                    fecha_vencimiento TEXT NOT NULL,
                    estado_cuota TEXT DEFAULT 'Pendiente',
                    FOREIGN KEY(socio_id) REFERENCES socios(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS conceptos_pago(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS actividades(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS programaciones(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    actividad_id INTEGER NOT NULL,
                    fecha_hora TEXT NOT NULL,
                    cupos_disponibles INTEGER,
                    FOREIGN KEY(actividad_id) REFERENCES actividades(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS reservas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    programacion_id INTEGER NOT NULL,
                    cliente_id INTEGER NOT NULL,
                    fecha_reserva TEXT NOT NULL,
                    estado TEXT NOT NULL DEFAULT 'Pendiente de pago',
                    FOREIGN KEY(programacion_id) REFERENCES programaciones(id),
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pagos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cuota_id INTEGER,
                    reserva_id INTEGER,
                    fecha_pago TEXT NOT NULL,
                    monto REAL NOT NULL,
                    concepto_id INTEGER NOT NULL,
                    metodo_id INTEGER NOT NULL,
                    FOREIGN KEY(cuota_id) REFERENCES cuotas(id),
                    FOREIGN KEY(reserva_id) REFERENCES reservas(id),
                    FOREIGN KEY(concepto_id) REFERENCES conceptos_pago(id),
                    CHECK ((cuota_id IS NOT NULL AND reserva_id IS NULL) OR (cuota_id IS NULL AND reserva_id IS NOT NULL))
                );
            """.trimIndent())

            // Verificar si el usuario 'Andrea' ya existe en 'usuarios'
            val checkCursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE LOWER(username) = 'andrea';", null)
            val userExists = checkCursor.moveToFirst() && checkCursor.getInt(0) > 0
            checkCursor.close()

            if (!userExists) {
                // SEMILLAS DE DATOS
                db.execSQL("INSERT OR IGNORE INTO roles(id, nombre) VALUES (1, 'admin'), (2, 'user');")

                db.execSQL("""
                    INSERT OR IGNORE INTO personas(id, nombre, apellido, dni) VALUES
                    (1, 'Andrea', 'Jiménez', '24753731'),
                    (2, 'Alberto', 'Campaner', '26333222'),
                    (3, 'David', 'Thomen', '12341234');
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO usuarios(id, persona_id, username, password, rol_id, activo) VALUES
                    (1, 1, 'Andrea', '1234', 1, 1),
                    (2, 2, 'Alberto', '1234', 1, 1),
                    (3, 3, 'David', '1234', 1, 1);
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO conceptos_pago(id, nombre) VALUES
                    (1, 'Inscripcion'), (2, 'Cuota'), (3, 'Actividad');
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO actividades(id, nombre, precio) VALUES
                    (1, 'Fútbol', 20000.0), (2, 'Básquet', 15000.0), (3, 'Vóley', 15000.0),
                    (4, 'Tenis', 25000.0), (5, 'Natación', 10000.0), (6, 'Karate', 8000.0),
                    (7, 'Taekwondo', 8000.0), (8, 'Gimnasio', 9000.0), (9, 'Musculación', 10000.0),
                    (10, 'Crossfit', 10000.0), (11, 'Yoga', 7000.0), (12, 'Pilates', 8500.0), (13, 'Zumba', 7500.0);
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO personas (nombre, apellido, dni) VALUES
                    ('María', 'González', '20123456'), ('Carlos', 'Rodríguez', '20234567'), ('Ana', 'Fernández', '20345678'), ('Luis', 'Martínez', '20456789'), ('Laura', 'Pérez', '20567890'),
                    ('Jorge', 'López', '20678901'), ('Marta', 'García', '20789012'), ('Pedro', 'Sánchez', '20890123'), ('Lucía', 'Ramírez', '20901234'), ('Diego', 'Torres', '20112345'),
                    ('Elena', 'Castro', '21123456'), ('Pablo', 'Ortiz', '21234567'), ('Sofía', 'Morales', '21345678'), ('Andrés', 'Vargas', '21456789'), ('Carolina', 'Mendoza', '21567890'),
                    ('Fernando', 'Herrera', '21678901'), ('Natalia', 'Medina', '21789012'), ('Ricardo', 'Flores', '21890123'), ('Patricia', 'Rojas', '21901234'), ('Esteban', 'Guzmán', '21112345'),
                    ('Valeria', 'Salazar', '22123456'), ('Maximiliano', 'Cruz', '22234567'), ('Camila', 'Reyes', '22345678'), ('Sebastián', 'Vega', '22456789'), ('Daniela', 'Molina', '22567890'),
                    ('Alejandro', 'Alvarado', '22678901'), ('Gabriela', 'Cordero', '22789012'), ('Felipe', 'Araya', '22890123'), ('Isabel', 'Pizarro', '22901234'), ('Cristian', 'Sepúlveda', '22112345'),
                    ('Macarena', 'Espinoza', '23123456'), ('Rodrigo', 'Tapia', '23234567'), ('Francisca', 'Gutiérrez', '23345678'), ('Mauricio', 'Maldonado', '23456789'), ('Nicole', 'Soto', '23567890'),
                    ('Alberto', 'Contreras', '23678901'), ('Katherine', 'Cáceres', '23789012'), ('Javier', 'Rivera', '23890123'), ('Constanza', 'Palma', '23901234'), ('Marcelo', 'Muñoz', '23112345'),
                    ('Andrea', 'Silva', '24123456'), ('Héctor', 'Barrientos', '24234567'), ('Paula', 'Leiva', '24345678'), ('Juan Pablo', 'Aranda', '24456789'), ('Tamara', 'Vergara', '24567890'),
                    ('Iván', 'Aguilera', '24678901'), ('Romina', 'Zúñiga', '24789012'), ('Oscar', 'Santibáñez', '24890123'), ('Claudia', 'Orellana', '24901234'), ('Bastián', 'Oyarzún', '24112345'),
                    ('Ximena', 'Mena', '25123456'), ('Adrián', 'Villalobos', '25234567'), ('Marisol', 'Pino', '25345678'), ('César', 'Carrasco', '25456789'), ('Javiera', 'Riquelme', '25567890'),
                    ('Francisco', 'Venegas', '25678901'), ('Rosa', 'Bustamante', '25789012'), ('Eduardo', 'Lara', '25890123'), ('Carla', 'Bravo', '25901234'), ('Gonzalo', 'Opazo', '25112345'),
                    ('Pía', 'Núñez', '26123456'), ('Fabián', 'Astudillo', '26234567'), ('Alicia', 'Escobar', '26345678'), ('Nicolás', 'Sandoval', '26456789'), ('Bárbara', 'Carreño', '26567890'),
                    ('Rafael', 'Toro', '26678901'), ('Carolina', 'Sáez', '26789012'), ('Emilio', 'Fuentes', '26890123'), ('Diana', 'Olivares', '26901234'), ('Eduardo', 'Cortés', '26112345'),
                    ('Mónica', 'Loyola', '27123456'), ('Patricio', 'Gallardo', '27234567'), ('Soledad', 'Retamal', '27345678'), ('Mario', 'Cordero', '27456789'), ('Angélica', 'Godoy', '27567890'),
                    ('Sergio', 'Henríquez', '27678901'), ('Maritza', 'Medel', '27789012'), ('Leonardo', 'Garrido', '27890123'), ('Nancy', 'Peralta', '27901234'), ('Elías', 'Vera', '27112345'),
                    ('Marcela', 'Valenzuela', '28123456'), ('Humberto', 'Delgado', '28234567'), ('Gloria', 'Parra', '28345678'), ('David', 'Santibáñez', '28456789'), ('Angélica', 'Arriagada', '28567890'),
                    ('Luis', 'Fuenzalida', '28678901'), ('Ingrid', 'Acuña', '28789012'), ('Juan Carlos', 'Ramos', '28890123'), ('Eliana', 'Chacón', '28901234'), ('Víctor', 'Pérez', '28112345'),
                    ('Cecilia', 'Alarcón', '29123456'), ('Guillermo', 'Correa', '29234567'), ('Beatriz', 'Martínez', '29345678'), ('Roberto', 'Navarro', '29456789'), ('Cristina', 'Díaz', '29567890'),
                    ('Manuel', 'Romero', '29678901'), ('Raquel', 'Molina', '29789012'), ('Joaquín', 'Hernández', '29890123'), ('Rocío', 'Gómez', '29901234'), ('Miguel', 'Álvarez', '29112345'),
                    ('Ainhoa', 'Sanz', '30123456');
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO clientes (persona_id, apto_fisico) 
                    SELECT id, 1 FROM personas WHERE id > 4;
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO socios (cliente_id, estado) 
                    SELECT id, 1 FROM clientes;
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO programaciones (actividad_id, fecha_hora, cupos_disponibles) VALUES
                    (1,'2026-07-15 10:00:00',20), (1,'2026-07-15 18:00:00',20), (1,'2026-07-15 20:00:00',20),
                    (1,'2026-07-20 10:00:00',20), (1,'2026-07-20 18:00:00',20), (1,'2026-07-20 20:00:00',20),
                    (2,'2026-07-16 09:00:00',15), (2,'2026-07-16 17:00:00',15), (2,'2026-07-16 19:00:00',15),
                    (3,'2026-07-15 11:00:00',18), (3,'2026-07-15 16:00:00',18), (3,'2026-07-15 20:00:00',18),
                    (4,'2026-07-16 08:00:00',8),  (4,'2026-07-16 10:00:00',8),  (4,'2026-07-16 18:00:00',8),
                    (5,'2026-07-15 07:00:00',25), (5,'2026-07-15 12:00:00',25), (5,'2026-07-15 19:00:00',25),
                    (6,'2026-07-16 17:00:00',15), (6,'2026-07-16 18:00:00',15), (6,'2026-07-16 20:00:00',15),
                    (7,'2026-07-15 16:00:00',15), (7,'2026-07-15 19:00:00',15), (7,'2026-07-15 21:00:00',15),
                    (8,'2026-07-16 07:00:00',30), (8,'2026-07-16 13:00:00',30), (8,'2026-07-16 18:00:00',30),
                    (9,'2026-07-17 08:00:00',20), (9,'2026-07-17 14:00:00',20), (9,'2026-07-17 19:00:00',20),
                    (10,'2026-07-15 06:30:00',12),(10,'2026-07-15 17:30:00',12),(10,'2026-07-15 19:30:00',12),
                    (11,'2026-07-16 09:00:00',20),(11,'2026-07-16 18:30:00',20),(11,'2026-07-16 20:00:00',20),
                    (12,'2026-07-15 09:00:00',18),(12,'2026-07-15 15:00:00',18),(12,'2026-07-15 18:00:00',18),
                    (13,'2026-07-16 10:00:00',25),(13,'2026-07-16 18:00:00',25),(13,'2026-07-16 20:00:00',25);
                """.trimIndent())

                db.execSQL("""
                    INSERT OR IGNORE INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
                    SELECT id, 1500.00, '2026-07-20', 'Pendiente' FROM socios;
                """.trimIndent())
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }
}
