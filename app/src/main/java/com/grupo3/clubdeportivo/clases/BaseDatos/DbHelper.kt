package com.grupo3.clubdeportivo.clases.BaseDatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import java.util.Date

/**
 * Helper principal para la creación, versión y sembrado de la base de datos SQLite "club_deportivo".
 *
 * Administra el esquema relacional para personas, clientes, socios, no socios, actividades,
 * reservas, cuotas de pago y usuarios del sistema.
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, "club_deportivo", null, 14) {

    /**
     * Habilita las restricciones de claves foráneas en la base de datos SQLite.
     */
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    /**
     * Se ejecuta al crear la base de datos por primera vez.
     * Crea todas las tablas e inserta los catálogos y datos iniciales de prueba.
     */
    override fun onCreate(db: SQLiteDatabase) {
        crearTablas(db)
        sembrarCatalogos(db)
    }

    /**
     * Se ejecuta cuando se incrementa la versión de la base de datos.
     * Elimina las tablas existentes y reejecuta onCreate para asegurar un entorno limpio.
     */
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        val tablas = listOf(
            "pagos", "reservas", "programaciones", "actividades", "metodos_pago", "cuotas",
            "carnet", "socios", "no_socios", "usuarios", "clientes", "personas", "roles"
        )

        tablas.forEach { tabla -> db.execSQL("DROP TABLE IF EXISTS $tabla") }
        onCreate(db)
    }

    /**
     * Crea la estructura completa de tablas relacionales de la aplicación.
     */
    private fun crearTablas(db: SQLiteDatabase) {
        // Tabla de Roles de Usuario (ej: admin, user)
        db.execSQL(
            """
                CREATE TABLE roles(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                );
            """.trimIndent()
        )

        // Tabla de Personas (datos personales genéricos)
        db.execSQL(
            """
                CREATE TABLE personas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellido TEXT NOT NULL,
                    dni TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL
                );
            """.trimIndent()
        )

        // Tabla de Clientes (vinculada a Persona, incluye apto físico)
        db.execSQL(
            """
                CREATE TABLE clientes(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL UNIQUE,
                    apto_fisico INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY(persona_id) REFERENCES personas(id)
                );
            """.trimIndent()
        )

        // Tabla de Usuarios para inicio de sesión en el sistema
        db.execSQL(
            """
                CREATE TABLE usuarios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL UNIQUE,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    rol_id INTEGER NOT NULL,
                    activo INTEGER DEFAULT 1,
                    FOREIGN KEY(persona_id) REFERENCES personas(id),
                    FOREIGN KEY(rol_id) REFERENCES roles(id)
                );
            """.trimIndent()
        )

        // Tabla de Socios del Club
        db.execSQL(
            """
                CREATE TABLE socios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL UNIQUE,
                    estado INTEGER DEFAULT 0,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent()
        )

        // Tabla de No Socios (clientes temporales)
        db.execSQL(
            """
                CREATE TABLE no_socios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL UNIQUE,
                    acceso_diario INTEGER DEFAULT 0,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent()
        )

        // Tabla de Carnet de Identificación de Socio
        db.execSQL(
            """
                CREATE TABLE carnet(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    socio_id INTEGER NOT NULL UNIQUE,
                    fecha_emision TEXT NOT NULL,
                    estado INTEGER DEFAULT 1,
                    FOREIGN KEY(socio_id) REFERENCES socios(id)
                );
            """.trimIndent()
        )

        // Tabla de Cuotas mensuales o de inscripción
        db.execSQL(
            """
                CREATE TABLE cuotas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    socio_id INTEGER NOT NULL,
                    monto_cuota REAL NOT NULL,
                    fecha_vencimiento TEXT NOT NULL,
                    estado_cuota TEXT DEFAULT 'Pendiente',
                    FOREIGN KEY(socio_id) REFERENCES socios(id)
                );
            """.trimIndent()
        )

        // Tabla de Métodos de Pago disponibles
        db.execSQL(
            """
                CREATE TABLE metodos_pago(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                );
            """.trimIndent()
        )

        // Tabla de Actividades deportivas
        db.execSQL(
            """
                CREATE TABLE actividades(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL
                );
            """.trimIndent()
        )

        // Tabla de Programaciones de actividades
        db.execSQL(
            """
                CREATE TABLE programaciones(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    actividad_id INTEGER NOT NULL,
                    fecha_hora TEXT NOT NULL,
                    cupos_disponibles INTEGER,
                    FOREIGN KEY(actividad_id) REFERENCES actividades(id)
                );
            """.trimIndent()
        )

        // Tabla de Reservas de actividades realizadas por clientes
        db.execSQL(
            """
                CREATE TABLE reservas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    programacion_id INTEGER NOT NULL,
                    cliente_id INTEGER NOT NULL,
                    fecha_reserva TEXT NOT NULL,
                    estado TEXT NOT NULL DEFAULT 'Pendiente de pago',
                    FOREIGN KEY(programacion_id) REFERENCES programaciones(id),
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                );
            """.trimIndent()
        )

        // Tabla de Registros de Pagos
        db.execSQL(
            """
                CREATE TABLE pagos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cuota_id INTEGER,
                    reserva_id INTEGER,
                    fecha_pago TEXT NOT NULL,
                    monto REAL NOT NULL,
                    metodo_id INTEGER NOT NULL,
                    FOREIGN KEY(cuota_id) REFERENCES cuotas(id),
                    FOREIGN KEY(reserva_id) REFERENCES reservas(id),
                    FOREIGN KEY(metodo_id) REFERENCES metodos_pago(id),
                    CHECK ((cuota_id IS NOT NULL AND reserva_id IS NULL) OR (cuota_id IS NULL AND reserva_id NOT NULL))
                );
            """.trimIndent()
        )
    }

    /**
     * Siembra los catálogos y registros iniciales de prueba en la base de datos.
     */
    private fun sembrarCatalogos(db: SQLiteDatabase) {
        // 1. Roles de usuario
        db.execSQL("INSERT INTO roles(id, nombre) VALUES (1, 'admin'), (2, 'user');")

        // 2. Personas administrativas base
        db.execSQL(
            """
            INSERT INTO personas(nombre, apellido, dni, email) VALUES
            ('Andrea', 'Jiménez', '24753731', 'andrea.jimenez@example.com'),
            ('Alberto', 'Campaner', '26333222', 'alberto.campaner@example.com'),
            ('David', 'Thomen', '12341234', 'david.thomen@example.com');
        """.trimIndent()
        )

        // 3. Usuarios administradores
        db.execSQL(
            """
                    INSERT INTO usuarios(id, persona_id, username, password, rol_id, activo) VALUES
                    (1, 1, 'Andrea', '1234', 1, 1),
                    (2, 2, 'Alberto', '1234', 1, 1),
                    (3, 3, 'David', '1234', 1, 1);
                """.trimIndent()
        )

        // 4. Métodos de pago disponibles
        db.execSQL(
            """
                    INSERT INTO metodos_pago(id, nombre) VALUES
                    (1, 'Efectivo'), (2, 'Transferencia'), (3, 'Tarjeta Crédito'), (4, 'Tarjeta Dédito'), (5, 'MercadoPago');
                """.trimIndent()
        )

        // 5. Catálogo de actividades y precios
        db.execSQL(
            """
                    INSERT INTO actividades(id, nombre, precio) VALUES
                    (1, 'Fútbol', 20000.0), (2, 'Básquet', 15000.0), (3, 'Vóley', 15000.0),
                    (4, 'Tenis', 25000.0), (5, 'Natación', 10000.0), (6, 'Karate', 8000.0),
                    (7, 'Taekwondo', 8000.0), (8, 'Gimnasio', 9000.0), (9, 'Musculación', 10000.0),
                    (10, 'Crossfit', 10000.0), (11, 'Yoga', 7000.0), (12, 'Pilates', 8500.0), (13, 'Zumba', 7500.0);
                """.trimIndent()
        )

        // 6. Lista masiva de personas (socios y no socios)
        db.execSQL(
            """
            INSERT INTO personas (nombre, apellido, dni, email) VALUES
            ('María', 'González', '20123456', 'maria.gonzalez@example.com'), ('Carlos', 'Rodríguez', '20234567', 'carlos.rodriguez@example.com'), ('Ana', 'Fernández', '20345678', 'ana.fernandez@example.com'),
            ('Luis', 'Martínez', '20456789', 'luis.martinez@example.com'), ('Laura', 'Pérez', '20567890', 'laura.perez@example.com'), ('Jorge', 'López', '20678901', 'jorge.lopez@example.com'),
            ('Marta', 'García', '20789012', 'marta.garcia@example.com'), ('Pedro', 'Sánchez', '20890123', 'pedro.sanchez@example.com'), ('Lucía', 'Ramírez', '20901234', 'lucia.ramirez@example.com'),
            ('Diego', 'Torres', '20112345', 'diego.torres@example.com'), ('Elena', 'Castro', '21123456', 'elena.castro@example.com'), ('Pablo', 'Ortiz', '21234567', 'pablo.ortiz@example.com'),
            ('Sofía', 'Morales', '21345678', 'sofia.morales@example.com'), ('Andrés', 'Vargas', '21456789', 'andres.vargas@example.com'), ('Carolina', 'Mendoza', '21567890', 'carolina.mendoza@example.com'),
            ('Fernando', 'Herrera', '21678901', 'fernando.herrera@example.com'), ('Natalia', 'Medina', '21789012', 'natalia.medina@example.com'), ('Ricardo', 'Flores', '21890123', 'ricardo.flores@example.com'),
            ('Patricia', 'Rojas', '21901234', 'patricia.rojas@example.com'), ('Esteban', 'Guzmán', '21112345', 'esteban.guzman@example.com'), ('Valeria', 'Salazar', '22123456', 'valeria.salazar@example.com'),
            ('Maximiliano', 'Cruz', '22234567', 'maximiliano.cruz@example.com'), ('Camila', 'Reyes', '22345678', 'camila.reyes@example.com'), ('Sebastián', 'Vega', '22456789', 'sebastian.vega@example.com'),
            ('Daniela', 'Molina', '22567890', 'daniela.molina@example.com'), ('Alejandro', 'Alvarado', '22678901', 'alejandro.alvarado@example.com'), ('Gabriela', 'Cordero', '22789012', 'gabriela.cordero@example.com'),
            ('Felipe', 'Araya', '22890123', 'felipe.araya@example.com'), ('Isabel', 'Pizarro', '22901234', 'isabel.pizarro@example.com'), ('Cristian', 'Sepúlveda', '22112345', 'cristian.sepulveda@example.com'),
            ('Macarena', 'Espinoza', '23123456', 'macarena.espinoza@example.com'), ('Rodrigo', 'Tapia', '23234567', 'rodrigo.tapia@example.com'), ('Francisca', 'Gutiérrez', '23345678', 'francisca.gutierrez@example.com'),
            ('Mauricio', 'Maldonado', '23456789', 'mauricio.maldonado@example.com'), ('Nicole', 'Soto', '23567890', 'nicole.soto@example.com'), ('Alberto', 'Contreras', '23678901', 'alberto.contreras@example.com'),
            ('Katherine', 'Cáceres', '23789012', 'katherine.caceres@example.com'), ('Javier', 'Rivera', '23890123', 'javier.rivera@example.com'), ('Constanza', 'Palma', '23901234', 'constanza.palma@example.com'),
            ('Marcelo', 'Muñoz', '23112345', 'marcelo.munoz@example.com'), ('Andrea', 'Silva', '24123456', 'andrea.silva@example.com'), ('Héctor', 'Barrientos', '24234567', 'hector.barrientos@example.com'),
            ('Paula', 'Leiva', '24345678', 'paula.leiva@example.com'), ('Juan Pablo', 'Aranda', '24456789', 'juanpablo.aranda@example.com'), ('Tamara', 'Vergara', '24567890', 'tamara.vergara@example.com'),
            ('Iván', 'Aguilera', '24678901', 'ivan.aguilera@example.com'), ('Romina', 'Zúñiga', '24789012', 'romina.zuniga@example.com'), ('Oscar', 'Santibáñez', '24890123', 'oscar.santibanez@example.com'),
            ('Claudia', 'Orellana', '24901234', 'claudia.orellana@example.com'), ('Bastián', 'Oyarzún', '24112345', 'bastian.oyarzun@example.com'), ('Ximena', 'Mena', '25123456', 'ximena.mena@example.com'),
            ('Adrián', 'Villalobos', '25234567', 'adrian.villalobos@example.com'), ('Marisol', 'Pino', '25345678', 'marisol.pino@example.com'), ('César', 'Carrasco', '25456789', 'cesar.carrasco@example.com'),
            ('Javiera', 'Riquelme', '25567890', 'javiera.riquelme@example.com'), ('Francisco', 'Venegas', '25678901', 'francisco.venegas@example.com'), ('Rosa', 'Bustamante', '25789012', 'rosa.bustamante@example.com'),
            ('Eduardo', 'Lara', '25890123', 'eduardo.lara@example.com'), ('Carla', 'Bravo', '25901234', 'carla.bravo@example.com'), ('Gonzalo', 'Opazo', '25112345', 'gonzalo.opazo@example.com'),
            ('Pía', 'Núñez', '26123456', 'pia.nunez@example.com'), ('Fabián', 'Astudillo', '26234567', 'fabian.astudillo@example.com'), ('Alicia', 'Escobar', '26345678', 'alicia.escobar@example.com'),
            ('Nicolás', 'Sandoval', '26456789', 'nicolas.sandoval@example.com'), ('Bárbara', 'Carreño', '26567890', 'barbara.carreno@example.com'), ('Rafael', 'Toro', '26678901', 'rafael.toro@example.com'),
            ('Carolina', 'Sáez', '26789012', 'carolina.saez@example.com'), ('Emilio', 'Fuentes', '26890123', 'emilio.fuentes@example.com'), ('Diana', 'Olivares', '26901234', 'diana.olivares@example.com'),
            ('Eduardo', 'Cortés', '26112345', 'eduardo.cortes@example.com'), ('Mónica', 'Loyola', '27123456', 'monica.loyola@example.com'), ('Patricio', 'Gallardo', '27234567', 'patricio.gallardo@example.com'),
            ('Soledad', 'Retamal', '27345678', 'soledad.retamal@example.com'), ('Mario', 'Cordero', '27456789', 'mario.cordero@example.com'), ('Angélica', 'Godoy', '27567890', 'angelica.godoy@example.com'),
            ('Sergio', 'Henríquez', '27678901', 'sergio.henriquez@example.com'), ('Maritza', 'Medel', '27789012', 'maritza.medel@example.com'), ('Leonardo', 'Garrido', '27890123', 'leonardo.garrido@example.com'),
            ('Nancy', 'Peralta', '27901234', 'nancy.peralta@example.com'), ('Elías', 'Vera', '27112345', 'elias.vera@example.com'), ('Marcela', 'Valenzuela', '28123456', 'marcela.valenzuela@example.com'),
            ('Humberto', 'Delgado', '28234567', 'humberto.delgado@example.com'), ('Gloria', 'Parra', '28345678', 'gloria.parra@example.com'), ('David', 'Santibáñez', '28456789', 'david.santibanez@example.com'),
            ('Angélica', 'Arriagada', '28567890', 'angelica.arriagada@example.com'), ('Luis', 'Fuenzalida', '28678901', 'luis.fuenzalida@example.com'), ('Ingrid', 'Acuña', '28789012', 'ingrid.acuna@example.com'),
            ('Juan Carlos', 'Ramos', '28890123', 'juancarlos.ramos@example.com'), ('Eliana', 'Chacón', '28901234', 'eliana.chacon@example.com'), ('Víctor', 'Pérez', '28112345', 'victor.perez@example.com'),
            ('Cecilia', 'Alarcón', '29123456', 'cecilia.alarcon@example.com'), ('Guillermo', 'Correa', '29234567', 'guillermo.correa@example.com'), ('Beatriz', 'Martínez', '29345678', 'beatriz.martinez@example.com'),
            ('Roberto', 'Navarro', '29456789', 'roberto.navarro@example.com'), ('Cristina', 'Díaz', '29567890', 'cristina.diaz@example.com'), ('Manuel', 'Romero', '29678901', 'manuel.romero@example.com'),
            ('Raquel', 'Molina', '29789012', 'raquel.molina@example.com'), ('Joaquín', 'Hernández', '29890123', 'joaquin.hernandez@example.com'), ('Rocío', 'Gómez', '29901234', 'rocio.gomez@example.com'),
            ('Miguel', 'Álvarez', '29112345', 'miguel.alvarez@example.com'), ('Ainhoa', 'Sanz', '30123456', 'ainhoa.sanz@example.com');
        """.trimIndent()
        )

        // 7. Registro de Clientes
        db.execSQL(
            """
                    INSERT INTO clientes (persona_id, apto_fisico) 
                    SELECT id, 1 FROM personas WHERE id > 3;
                """.trimIndent()
        )

        // 8. Registro de No Socios (clientes temporales)
        db.execSQL(
            """
            INSERT INTO no_socios (cliente_id, acceso_diario)
            SELECT cl.id, 0
            FROM clientes cl
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni IN ('20123456', '20234567', '20345678');
        """.trimIndent()
        )

        // 9. Registro de Socios (resto de clientes)
        db.execSQL(
            """
            INSERT INTO socios (cliente_id, estado)
            SELECT id, 1 FROM clientes
            WHERE id NOT IN (SELECT cliente_id FROM no_socios);
            """.trimIndent()
        )

        // 10. Socios inactivos de prueba
        db.execSQL(
            """
            UPDATE socios SET estado = 0
            WHERE cliente_id IN (
                SELECT cl.id FROM clientes cl
                INNER JOIN personas p ON cl.persona_id = p.id
                WHERE p.dni IN ('20456789', '20567890', '20678901')
            );
            """.trimIndent()
        )

        // 11. Programaciones semanales de actividades

        db.execSQL(
            """
            WITH RECURSIVE fechas(fecha) AS (
                SELECT '2026-11-11'
                UNION ALL
                SELECT date(fecha, '+1 day')
                FROM fechas
                WHERE fecha < '2026-11-28'
            ),
            horarios(actividad_id, hora1, hora2, cupos) AS (
                VALUES
                    (1, '10:00:00', '18:00:00', 20),
                    (2, '09:00:00', '17:00:00', 15),
                    (3, '11:00:00', '16:00:00', 18),
                    (4, '08:00:00', '18:00:00', 8),
                    (5, '07:00:00', '12:00:00', 25),
                    (6, '10:00:00', '17:00:00', 15),
                    (7, '09:00:00', '19:00:00', 15),
                    (8, '07:00:00', '13:00:00', 30),
                    (9, '08:00:00', '14:00:00', 20),
                    (10, '06:30:00', '17:30:00', 12),
                    (11, '09:00:00', '18:00:00', 20),
                    (12, '09:00:00', '15:00:00', 18),
                    (13, '10:00:00', '18:00:00', 25)
            )
            INSERT INTO programaciones (
                actividad_id,
                fecha_hora,
                cupos_disponibles
            )
            SELECT
                h.actividad_id,
                f.fecha || ' ' || t.hora,
                h.cupos
            FROM fechas f
            CROSS JOIN horarios h
            CROSS JOIN (
                SELECT actividad_id, hora1 AS hora FROM horarios
                UNION ALL
                SELECT actividad_id, hora2 AS hora FROM horarios
            ) t
            WHERE t.actividad_id = h.actividad_id;
    """.trimIndent()
        )


        // 12. Sembrado de cuotas del día para todos los socios
        sembrarCuotasVencidasHoy(db)

        // 13. Inserción de cuotas pagadas de prueba para la socia Marta García (DNI: 20789012)
        db.execSQL(
            """
            INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
            SELECT s.id, 15000.00, '2025-01-15', 'Pagada'
            FROM socios s
            INNER JOIN clientes cl ON s.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni = '20789012';
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
            SELECT s.id, 15000.00, '2025-02-15', 'Pagada'
            FROM socios s
            INNER JOIN clientes cl ON s.cliente_id = cl.id
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni = '20789012';
            """.trimIndent()
        )
    }

    /**
     * Siembra o renueva las cuotas vencidas del día de hoy para todos los socios activos.
     * Elimina previamente cuotas duplicadas con la fecha de hoy antes de realizar el sembrado.
     *
     * @param db Instancia de SQLiteDatabase opcional. Si no se provee, utiliza writableDatabase.
     */
    fun sembrarCuotasVencidasHoy(db: SQLiteDatabase? = null) {
        val database = db ?: writableDatabase
        val fechaHoyIso = FechaDb.formatearFechaIso(Date())
        database.execSQL("DELETE FROM cuotas WHERE date(fecha_vencimiento) = date('$fechaHoyIso');")
        database.execSQL(
            """
            INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
            SELECT id, 15000.00, '$fechaHoyIso', 'Pendiente' FROM socios;
            """.trimIndent()
        )
    }
}
