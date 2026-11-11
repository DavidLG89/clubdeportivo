# 🏆 Club Deportivo - Sistema de Gestión de Socios, No Socios, Pagos y Reservas

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg)
![Database](https://img.shields.io/badge/Database-SQLite-blue.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

**Club Deportivo** es una aplicación móvil nativa desarrollada en **Kotlin** para la plataforma **Android**, diseñada para la administración integral de un club o centro deportivo. Permite gestionar **socios y no socios**, emisión de carnets digitales, control de vencimientos de cuotas, cobro de pases diarios, reservas de actividades deportivas y la generación/impresión de comprobantes y reportes en PDF.

---

## ✨ Características Principales

### 🔐 1. Autenticación y Control de Accesos
* **Inicio de Sesión**: Inicio de sesión seguro por usuario y contraseña con control de roles (`admin` / `user`).
* **Recuperación de Contraseña**: Módulo de verificación de correo e identidad para restablecimiento de clave.

### 💳 2. Gestión de Socios, No Socios y Carnet Digital
* **Registro de Clientes**: Formulario de alta con validación de apto físico y firma/aceptación de contrato.
* **Diferenciación de Clientes**: Gestión segmentada entre **Socios** (cuotas mensuales de membresía) y **No Socios** (pases temporales y cobro de acceso diario).
* **Impresión de Carnet de Socio**: Generación en vista previa e impresión de carnet físico/digital con bordes redondeados y código visual.

### 📅 3. Módulo de Vencimientos y Cuotas
* **Vencimiento Diario**: Consulta en tabla con scrollbar de todas las cuotas vencidas en el día actual.
* **Búsqueda por DNI**: Consulta del historial completo de cuotas (Pagadas y Pendientes) ingresando el DNI del socio.
* **Reportes Multipágina en PDF**: Exportación e impresión nativa de reportes de cuotas divididos dinámicamente en páginas con encabezado, pie y numeración (*Página X de Y*).

### ⚽ 4. Reservas de Actividades Deportivas
* **Catálogo de Actividades**: Fútbol, Básquet, Vóley, Tenis, Natación, Gimnasio, Karate, Taekwondo, Crossfit, Yoga, Pilates, Zumba, entre otras.
* **Programación y Cupos**: Selección de días, horarios y verificación automática de cupos disponibles.
* **Comprobantes de Reserva**: Emisión e impresión del comprobante de reserva en PDF.

### 💵 5. Módulo de Pagos
* **Múltiples Métodos de Pago**: Efectivo, Transferencia Bancaria, Tarjeta de Crédito, Tarjeta de Débito y MercadoPago.
* **Pago en Cuotas**: Selección de plan de cuotas para formalización de cobros.
* **Comprobantes de Pago**: Generación de recibo oficial con opción de retorno al menú principal.

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue una arquitectura limpia orientada a capas (*Layered Architecture*) que separa las responsabilidades de datos, lógica de negocio y presentación:

```
com.grupo3.clubdeportivo
├── clases
│   ├── BaseDatos       # DbHelper (SQLiteOpenHelper), gestión de tablas y sembrado de datos
│   ├── DTOS            # Data Transfer Objects (CuotaDTO, ReservaDTO, ClienteDTO, etc.)
│   ├── Helpers         # Formateadores de fechas (FechaDb) y utilidades
│   ├── Modelos         # Clases de dominio (Persona, Cliente, Socio, NoSocio, Cuota, Carnet)
│   ├── Repositorios    # Capa de datos y consultas SQL (CuotaRepo, VencimientoRepo, ClienteRepo)
│   └── Servicios       # Capa de negocio (CuotaServ, VencimientoServ, ReservaServ, ClienteServ)
└── ui
    ├── carnet          # Vista previa e impresión de Carnet de Socio
    ├── fragmentos      # Componentes reutilizables (Búsqueda DNI, Métodos de Pago, Contratos)
    ├── pagoCuota       # Pantalla de cobro de cuotas
    ├── pagoReserva     # Pantalla de cobro de reservas
    ├── registroCliente # Formulario y contrato de alta de cliente
    └── vencimiento     # Vencimiento diario, búsqueda por DNI y menú de vencimientos
```

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje**: [Kotlin](https://kotlinlang.org/)
* **Min SDK**: 24 (Android 7.0 Nougat)
* **Target SDK**: 34 / 35 (Android 14 / 15)
* **Base de Datos**: SQLite nativo (`SQLiteOpenHelper`, `SQLiteDatabase`)
* **UI & Layouts**: XML Layouts, Material Components / Material 3, `ConstraintLayout`, `TableLayout`, `ScrollView`
* **Arquitectura Android**: `ViewModel`, `FragmentManager`, `ViewCompat`, `WindowInsetsCompat`
* **Impresión y PDF**: `android.print.PrintManager`, `android.graphics.pdf.PdfDocument`, `PrintDocumentAdapter`

---

## 🚀 Instrucciones de Instalación y Ejecución

### Requisitos Previos
1. **Android Studio** (Ladybug, Koala, Jellyfish o superior).
2. **JDK 17** o superior configurado en Android Studio.
3. Dispositivo físico o Emulador Android con SDK 24 o superior.

### Pasos para Ejecutar

1. **Clonar o Descargar el Repositorio**:
   ```bash
   git clone https://github.com/tu-usuario/clubdeportivo.git
   ```

2. **Abrir en Android Studio**:
   - Abre Android Studio, selecciona **File > Open** y navega hasta la carpeta del proyecto.

3. **Sincronizar Gradle**:
   - Deja que Gradle descargue las dependencias y sincronice el proyecto automáticamente.

4. **Compilar y Ejecutar**:
   - Selecciona tu emulador o dispositivo físico en la barra superior.
   - Haz clic en **Run 'app'** (▶) o ejecuta el siguiente comando en la terminal:
     ```bash
     ./gradlew app:assembleDebug
     ```

---

## 🔑 Datos de Prueba Iniciales (Sembrado)

Al ejecutar la aplicación por primera vez, SQLite genera automáticamente la base de datos con los siguientes registros de prueba:

* **Usuario Administrador**:
  * **Usuario**: `Andrea`
  * **Contraseña**: `1234`

* **Socio de Prueba con Cuotas Pagadas (Historial Completo)**:
  * **DNI**: `20789012` (Marta García)

* **Socios de Prueba con Cuotas Pendientes**:
  * **DNI**: `24753731` (Andrea Jiménez)
  * **DNI**: `20112345` (Diego Torres)

* **No Socios de Prueba (Pase Diario - CON Apto Físico / Habilitado)**:
  * **DNI**: `20123456` (María González - Apto Físico: `1`)
  * **DNI**: `20234567` (Carlos Rodríguez - Apto Físico: `1`)

* **No Socio de Prueba (Pase Diario - SIN Apto Físico / Deshabilitado)**:
  * **DNI**: `20345678` (Ana Fernández - Apto Físico: `0`)

---

## 👥 Equipo de Desarrollo

* **Grupo 3** - *Desarrollo de Aplicaciones Móviles / Proyecto Club Deportivo*

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo `LICENSE`.
