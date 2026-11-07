package com.grupo3.clubdeportivo.clases.BaseDatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "club_deportivo", null, 10) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        crearTablas(db)
        sembrarCatalogos(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        var tablas = listOf(
            "pagos", "reservas", "programaciones", "actividades", "metodos_pago", "cuotas",
            "carnet", "socios", "no_socios", "usuarios", "clientes", "personas", "roles"
        )

        tablas.forEach { tabla -> db.execSQL("DROP TABLE IF EXISTS ${tabla}") }
        onCreate(db)
    }

    private fun crearTablas(db: SQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE roles(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                );
            """.trimIndent()
        )

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

        db.execSQL(
            """
                CREATE TABLE metodos_pago(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL
                );
            """.trimIndent()
        )

        db.execSQL(
            """
                CREATE TABLE actividades(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL
                );
            """.trimIndent()
        )

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
                    CHECK ((cuota_id IS NOT NULL AND reserva_id IS NULL) OR (cuota_id IS NULL AND reserva_id IS NOT NULL))
                );
            """.trimIndent()
        )
    }

    private fun sembrarCatalogos(db: SQLiteDatabase) {
        db.execSQL("INSERT INTO roles(id, nombre) VALUES (1, 'admin'), (2, 'user');")

        db.execSQL(
            """
            INSERT INTO personas(nombre, apellido, dni, email) VALUES
            ('Andrea', 'Jiménez', '24753731', 'andrea.jimenez@example.com'),
            ('Alberto', 'Campaner', '26333222', 'alberto.campaner@example.com'),
            ('David', 'Thomen', '12341234', 'david.thomen@example.com');
        """.trimIndent()
        )

        db.execSQL(
            """
                    INSERT INTO usuarios(id, persona_id, username, password, rol_id, activo) VALUES
                    (1, 1, 'Andrea', '1234', 1, 1),
                    (2, 2, 'Alberto', '1234', 1, 1),
                    (3, 3, 'David', '1234', 1, 1);
                """.trimIndent()
        )

        db.execSQL(
            """
                    INSERT INTO metodos_pago(id, nombre) VALUES
                    (1, 'Efectivo'), (2, 'Transferencia'), (3, 'Tarjeta Crédito'), (4, 'Tarjeta Dédito'), (5, 'MercadoPago');
                """.trimIndent()
        )

        db.execSQL(
            """
                    INSERT INTO actividades(id, nombre, precio) VALUES
                    (1, 'Fútbol', 20000.0), (2, 'Básquet', 15000.0), (3, 'Vóley', 15000.0),
                    (4, 'Tenis', 25000.0), (5, 'Natación', 10000.0), (6, 'Karate', 8000.0),
                    (7, 'Taekwondo', 8000.0), (8, 'Gimnasio', 9000.0), (9, 'Musculación', 10000.0),
                    (10, 'Crossfit', 10000.0), (11, 'Yoga', 7000.0), (12, 'Pilates', 8500.0), (13, 'Zumba', 7500.0);
                """.trimIndent()
        )

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

        db.execSQL(
            """
                    INSERT INTO clientes (persona_id, apto_fisico) 
                    SELECT id, 1 FROM personas WHERE id > 3;
                """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO no_socios (cliente_id, acceso_diario)
            SELECT cl.id, 0
            FROM clientes cl
            INNER JOIN personas p ON cl.persona_id = p.id
            WHERE p.dni IN ('20123456', '20234567', '20345678');
        """.trimIndent()
            )


        db.execSQL(
            """
    INSERT INTO socios (cliente_id, estado)
    SELECT id, 1 FROM clientes
    WHERE id NOT IN (SELECT cliente_id FROM no_socios);
    """.trimIndent()
        )

// NUEVO: socios inactivos
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

// ... INSERT INTO programaciones (sin cambios) ...

        db.execSQL(
            """
    INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
    SELECT id, 1500.00, '2026-07-20', 'Pendiente' FROM socios;
    """.trimIndent()
        )

// NUEVO: socio activo con cuota paga
        db.execSQL(
            """
    UPDATE cuotas SET estado_cuota = 'Pagada', fecha_vencimiento = '2026-12-20'
    WHERE socio_id IN (
        SELECT s.id FROM socios s
        INNER JOIN clientes cl ON s.cliente_id = cl.id
        INNER JOIN personas p ON cl.persona_id = p.id
        WHERE p.dni = '20789012'
    );
    """.trimIndent()
        )

        db.execSQL(
    """
                    INSERT INTO programaciones (actividad_id, fecha_hora, cupos_disponibles) VALUES

        (1, '2026-11-04 10:00:00', 20), (1, '2026-11-04 18:00:00', 20),
        (1, '2026-11-05 10:00:00', 20), (1, '2026-11-05 18:00:00', 20),
        (1, '2026-11-06 10:00:00', 20), (1, '2026-11-06 18:00:00', 20),
        (1, '2026-11-07 10:00:00', 20), (1, '2026-11-07 18:00:00', 20),
        (1, '2026-11-08 10:00:00', 20), (1, '2026-11-08 18:00:00', 20),
        (1, '2026-11-09 10:00:00', 20), (1, '2026-11-09 18:00:00', 20),
        (1, '2026-11-10 10:00:00', 20), (1, '2026-11-10 18:00:00', 20),

        (2, '2026-11-04 09:00:00', 15), (2, '2026-11-04 17:00:00', 15),
        (2, '2026-11-05 09:00:00', 15), (2, '2026-11-05 17:00:00', 15),
        (2, '2026-11-06 09:00:00', 15), (2, '2026-11-06 17:00:00', 15),
        (2, '2026-11-07 09:00:00', 15), (2, '2026-11-07 17:00:00', 15),
        (2, '2026-11-08 09:00:00', 15), (2, '2026-11-08 17:00:00', 15),
        (2, '2026-11-09 09:00:00', 15), (2, '2026-11-09 17:00:00', 15),
        (2, '2026-11-10 09:00:00', 15), (2, '2026-11-10 17:00:00', 15),

        (3, '2026-11-04 11:00:00', 18), (3, '2026-11-04 16:00:00', 18),
        (3, '2026-11-05 11:00:00', 18), (3, '2026-11-05 16:00:00', 18),
        (3, '2026-11-06 11:00:00', 18), (3, '2026-11-06 16:00:00', 18),
        (3, '2026-11-07 11:00:00', 18), (3, '2026-11-07 16:00:00', 18),
        (3, '2026-11-08 11:00:00', 18), (3, '2026-11-08 16:00:00', 18),
        (3, '2026-11-09 11:00:00', 18), (3, '2026-11-09 16:00:00', 18),
        (3, '2026-11-10 11:00:00', 18), (3, '2026-11-10 16:00:00', 18),

        (4, '2026-11-04 08:00:00', 8), (4, '2026-11-04 18:00:00', 8),
        (4, '2026-11-05 08:00:00', 8), (4, '2026-11-05 18:00:00', 8),
        (4, '2026-11-06 08:00:00', 8), (4, '2026-11-06 18:00:00', 8),
        (4, '2026-11-07 08:00:00', 8), (4, '2026-11-07 18:00:00', 8),
        (4, '2026-11-08 08:00:00', 8), (4, '2026-11-08 18:00:00', 8),
        (4, '2026-11-09 08:00:00', 8), (4, '2026-11-09 18:00:00', 8),
        (4, '2026-11-10 08:00:00', 8), (4, '2026-11-10 18:00:00', 8),

        (5, '2026-11-04 07:00:00', 25), (5, '2026-11-04 12:00:00', 25),
        (5, '2026-11-05 07:00:00', 25), (5, '2026-11-05 12:00:00', 25),
        (5, '2026-11-06 07:00:00', 25), (5, '2026-11-06 12:00:00', 25),
        (5, '2026-11-07 07:00:00', 25), (5, '2026-11-07 12:00:00', 25),
        (5, '2026-11-08 07:00:00', 25), (5, '2026-11-08 12:00:00', 25),
        (5, '2026-11-09 07:00:00', 25), (5, '2026-11-09 12:00:00', 25),
        (5, '2026-11-10 07:00:00', 25), (5, '2026-11-10 12:00:00', 25),

        (6, '2026-11-04 10:00:00', 15), (6, '2026-11-04 17:00:00', 15),
        (6, '2026-11-05 10:00:00', 15), (6, '2026-11-05 17:00:00', 15),
        (6, '2026-11-06 10:00:00', 15), (6, '2026-11-06 17:00:00', 15),
        (6, '2026-11-07 10:00:00', 15), (6, '2026-11-07 17:00:00', 15),
        (6, '2026-11-08 10:00:00', 15), (6, '2026-11-08 17:00:00', 15),
        (6, '2026-11-09 10:00:00', 15), (6, '2026-11-09 17:00:00', 15),
        (6, '2026-11-10 10:00:00', 15), (6, '2026-11-10 17:00:00', 15),

        (7, '2026-11-04 09:00:00', 15), (7, '2026-11-04 19:00:00', 15),
        (7, '2026-11-05 09:00:00', 15), (7, '2026-11-05 19:00:00', 15),
        (7, '2026-11-06 09:00:00', 15), (7, '2026-11-06 19:00:00', 15),
        (7, '2026-11-07 09:00:00', 15), (7, '2026-11-07 19:00:00', 15),
        (7, '2026-11-08 09:00:00', 15), (7, '2026-11-08 19:00:00', 15),
        (7, '2026-11-09 09:00:00', 15), (7, '2026-11-09 19:00:00', 15),
        (7, '2026-11-10 09:00:00', 15), (7, '2026-11-10 19:00:00', 15),

        (8, '2026-11-04 07:00:00', 30), (8, '2026-11-04 13:00:00', 30),
        (8, '2026-11-05 07:00:00', 30), (8, '2026-11-05 13:00:00', 30),
        (8, '2026-11-06 07:00:00', 30), (8, '2026-11-06 13:00:00', 30),
        (8, '2026-11-07 07:00:00', 30), (8, '2026-11-07 13:00:00', 30),
        (8, '2026-11-08 07:00:00', 30), (8, '2026-11-08 13:00:00', 30),
        (8, '2026-11-09 07:00:00', 30), (8, '2026-11-09 13:00:00', 30),
        (8, '2026-11-10 07:00:00', 30), (8, '2026-11-10 13:00:00', 30),

        (9, '2026-11-04 08:00:00', 20), (9, '2026-11-04 14:00:00', 20),
        (9, '2026-11-05 08:00:00', 20), (9, '2026-11-05 14:00:00', 20),
        (9, '2026-11-06 08:00:00', 20), (9, '2026-11-06 14:00:00', 20),
        (9, '2026-11-07 08:00:00', 20), (9, '2026-11-07 14:00:00', 20),
        (9, '2026-11-08 08:00:00', 20), (9, '2026-11-08 14:00:00', 20),
        (9, '2026-11-09 08:00:00', 20), (9, '2026-11-09 14:00:00', 20),
        (9, '2026-11-10 08:00:00', 20), (9, '2026-11-10 14:00:00', 20),

        (10, '2026-11-04 06:30:00', 12), (10, '2026-11-04 17:30:00', 12),
        (10, '2026-11-05 06:30:00', 12), (10, '2026-11-05 17:30:00', 12),
        (10, '2026-11-06 06:30:00', 12), (10, '2026-11-06 17:30:00', 12),
        (10, '2026-11-07 06:30:00', 12), (10, '2026-11-07 17:30:00', 12),
        (10, '2026-11-08 06:30:00', 12), (10, '2026-11-08 17:30:00', 12),
        (10, '2026-11-09 06:30:00', 12), (10, '2026-11-09 17:30:00', 12),
        (10, '2026-11-10 06:30:00', 12), (10, '2026-11-10 17:30:00', 12),

        (11, '2026-11-04 09:00:00', 20), (11, '2026-11-04 18:00:00', 20),
        (11, '2026-11-05 09:00:00', 20), (11, '2026-11-05 18:00:00', 20),
        (11, '2026-11-06 09:00:00', 20), (11, '2026-11-06 18:00:00', 20),
        (11, '2026-11-07 09:00:00', 20), (11, '2026-11-07 18:00:00', 20),
        (11, '2026-11-08 09:00:00', 20), (11, '2026-11-08 18:00:00', 20),
        (11, '2026-11-09 09:00:00', 20), (11, '2026-11-09 18:00:00', 20),
        (11, '2026-11-10 09:00:00', 20), (11, '2026-11-10 18:00:00', 20),

        (12, '2026-11-04 09:00:00', 18), (12, '2026-11-04 15:00:00', 18),
        (12, '2026-11-05 09:00:00', 18), (12, '2026-11-05 15:00:00', 18),
        (12, '2026-11-06 09:00:00', 18), (12, '2026-11-06 15:00:00', 18),
        (12, '2026-11-07 09:00:00', 18), (12, '2026-11-07 15:00:00', 18),
        (12, '2026-11-08 09:00:00', 18), (12, '2026-11-08 15:00:00', 18),
        (12, '2026-11-09 09:00:00', 18), (12, '2026-11-09 15:00:00', 18),
        (12, '2026-11-10 09:00:00', 18), (12, '2026-11-10 15:00:00', 18),
        
        (13, '2026-11-04 10:00:00', 25), (13, '2026-11-04 18:00:00', 25),
        (13, '2026-11-05 10:00:00', 25), (13, '2026-11-05 18:00:00', 25),
        (13, '2026-11-06 10:00:00', 25), (13, '2026-11-06 18:00:00', 25),
        (13, '2026-11-07 10:00:00', 25), (13, '2026-11-07 18:00:00', 25),
        (13, '2026-11-08 10:00:00', 25), (13, '2026-11-08 18:00:00', 25),
        (13, '2026-11-09 10:00:00', 25), (13, '2026-11-09 18:00:00', 25),
        (13, '2026-11-10 10:00:00', 25), (13, '2026-11-10 18:00:00', 25);
        """.trimIndent()
)

        // Cuotas de prueba para la pantalla de vencimientos del día
        db.execSQL(
            """
    INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
    SELECT id, 30000.00, date('now','localtime'), 'Pendiente'
    FROM socios LIMIT 5;
    """.trimIndent()
        )

// Una pagada hoy, para comprobar que "todos los del día" mezcla estados
        db.execSQL(
            """
    INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
    SELECT id, 30000.00, date('now','localtime'), 'Pagada'
    FROM socios LIMIT 2 OFFSET 5;
    """.trimIndent()
        )

// Una de mañana, que NO debería aparecer en la pantalla
        db.execSQL(
            """
    INSERT INTO cuotas (socio_id, monto_cuota, fecha_vencimiento, estado_cuota)
    SELECT id, 30000.00, date('now','localtime','+1 day'), 'Pendiente'
    FROM socios LIMIT 1 OFFSET 7;
    """.trimIndent()
        )
    }
}