package com.grupo3.clubdeportivo.ui.vencimiento

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.clases.BaseDatos.DbHelperObject
import com.grupo3.clubdeportivo.clases.Helpers.FechaDb
import com.grupo3.clubdeportivo.clases.Repositorios.VencimientoRepo
import com.grupo3.clubdeportivo.clases.Servicios.VencimientoServ
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import java.io.FileOutputStream
import java.util.Date
import kotlin.collections.get
import kotlin.math.ceil

/**
 * Actividad que muestra y gestiona la lista de cuotas de socios que vencen en el día de hoy.
 *
 * Utiliza [VencimientoServ] para consultar las cuotas vencidas, ofreciendo visualización
 * en tabla con scrollbar e impresión en PDF multipágina con numeración.
 */
class VencimientoDiarioActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var fechaHoyActual: String
    private val dbHelper by lazy { DbHelperObject.get(this) }
    private val vencimientoServ by lazy { VencimientoServ(VencimientoRepo(dbHelper)) }
   private var cuotas: List<List<String>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vencimiento_diario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del botón de retroceso
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Obtener fecha actual usando el Helper FechaDb
        fechaHoyActual = FechaDb.formatearFecha(Date())

        val tvFechaHoy = findViewById<TextView>(R.id.tvFechaHoy)
        tvFechaHoy.text = "Fecha: $fechaHoyActual"

        // Referencias a vistas
        tableLayout = findViewById(R.id.tableLayout)
        val scrollViewTable = findViewById<ScrollView>(R.id.scrollViewTable)
        val btnImprimir = findViewById<Button>(R.id.btnImprimir)
        val tvError = findViewById<TextView>(R.id.tvError)



        val resultado = vencimientoServ.obtenerVencimientosPorFecha(Date())

        if (resultado.ok && resultado.cuotas.isNotEmpty()) {
            cuotas = resultado.cuotas.map { item ->
                val idStr = item.idCuota.toString()
                val nroSocioStr = item.socio?.idSocio?.toString() ?: "-"
                val nombreCompleto = "${item.socio?.nombre} ${item.socio?.apellido}".trim()
                val montoStr = "$${item.montoCuota.toInt()}"
                listOf(idStr, nroSocioStr, nombreCompleto, montoStr)
            }
            tvError.visibility = View.GONE
            scrollViewTable.visibility = View.VISIBLE
            btnImprimir.visibility = View.VISIBLE
            setupTable()
        } else {
            cuotas = emptyList()
            scrollViewTable.visibility = View.GONE
            btnImprimir.visibility = View.GONE
            tvError.visibility = View.VISIBLE
            tvError.text = "No se encontraron cuotas vencidas el día de hoy ($fechaHoyActual)"
        }

        btnImprimir.setOnClickListener {
            imprimirCuotas()
        }

        // Navegación al Perfil del Administrador desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, PerfilAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación al Menú Principal desde la barra inferior (Inicio)
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Construye las filas y celdas de la tabla en pantalla con los datos de las cuotas vencidas.
     */
    private fun setupTable() {
        tableLayout.removeAllViews()
        val headerRow = TableRow(this)
        val headers = listOf("Id", "Nro Socio", "Nombre y Apellido", "Monto")

        for (texto in headers) {
            val textView = TextView(this).apply {
                text = texto
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                textSize = 12f
                setPadding(5, 8, 5, 8)
                setBackgroundResource(R.drawable.fondo_header_tabla)
            }
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        for (cuota in cuotas) {
            val tableRow = TableRow(this)
            for (dato in cuota) {
                val textView = TextView(this).apply {
                    text = dato
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                    textSize = 12f
                    setPadding(5, 12, 5, 12)
                    setBackgroundResource(R.drawable.fondo_celda)
                }
                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
    }

    /**
     * Inicia la impresión en PDF de las cuotas vencidas del día utilizando [PrintManager].
     */
    private fun imprimirCuotas() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        val nombreDocumento = "Cuotas_vencidas_del_dia_$fechaHoyActual"

        printManager.print(
            nombreDocumento,
            CuotasPrintAdapter(),
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        )
    }

    /**
     * Muestra un diálogo emergente al finalizar la impresión ofreciendo regresar al Inicio o permanecer.
     */
    private fun mostrarDialogoOpciones() {
        AlertDialog.Builder(this)
            .setTitle("Impresión finalizada")
            .setMessage("¿Desea volver al menú principal o continuar en esta pantalla?")
            .setPositiveButton("Ir al Inicio") { _, _ ->
                val intent = Intent(this, MenuPrincipalActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Continuar aquí", null)
            .show()
    }

    /**
     * Adaptador para renderizar el reporte PDF multipágina de cuotas vencidas del día.
     */
    private inner class CuotasPrintAdapter : PrintDocumentAdapter() {

        private val rowsPerPage = 15

        override fun onFinish() {
            super.onFinish()
            runOnUiThread {
                mostrarDialogoOpciones()
            }
        }

        /**
         * Notifica a la API de impresión la cantidad total de páginas calculadas dinámicamente.
         */
        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }

            val totalPages = ceil(cuotas.size.toDouble() / rowsPerPage).toInt().coerceAtLeast(1)

            val info = PrintDocumentInfo.Builder("cuotas_vencidas_del_dia.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(totalPages)
                .build()

            callback.onLayoutFinished(info, true)
        }

        /**
         * Dibuja los elementos visuales, tabla, registros y numeración de página en el canvas del PDF.
         */
        override fun onWrite(
            pages: Array<out PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback
        ) {
            try {
                val pdfDocument = PdfDocument()
                val pageWidth = 595
                val pageHeight = 842
                val totalPages = ceil(cuotas.size.toDouble() / rowsPerPage).toInt().coerceAtLeast(1)

                val margenIzquierdo = 40f
                val margenDerecho = 40f
                val anchoTabla = pageWidth - margenIzquierdo - margenDerecho
                val anchoId = 55f
                val anchoSocio = 90f
                val anchoNombre = 260f
                val anchoMonto = anchoTabla - anchoId - anchoSocio - anchoNombre
                val altoFila = 38f

                val x1 = margenIzquierdo
                val x2 = x1 + anchoId
                val x3 = x2 + anchoSocio
                val x4 = x3 + anchoNombre
                val x5 = x4 + anchoMonto

                for (pageIndex in 0 until totalPages) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback.onWriteFailed("Cancelado")
                        return
                    }

                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas

                    val paint = Paint().apply {
                        isAntiAlias = true
                        color = Color.BLACK
                    }

                    var y = 50f

                    // TÍTULOS
                    paint.textSize = 22f
                    paint.typeface = Typeface.DEFAULT_BOLD
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText("CLUB DEPORTIVO", pageWidth / 2f, y, paint)

                    y += 35f
                    paint.textSize = 18f
                    canvas.drawText("CUOTAS VENCIDAS DEL DÍA", pageWidth / 2f, y, paint)

                    // FECHA DINÁMICA
                    y += 24f
                    paint.textSize = 13f
                    paint.typeface = Typeface.DEFAULT
                    canvas.drawText("Fecha: $fechaHoyActual", pageWidth / 2f, y, paint)

                    y += 20f

                    // LÍNEA DIVISORIA
                    paint.strokeWidth = 1f
                    canvas.drawLine(margenIzquierdo, y, pageWidth - margenDerecho, y, paint)

                    y += 30f

                    // ENCABEZADO TABLA PDF
                    paint.style = Paint.Style.FILL
                    paint.color = Color.rgb(169, 212, 244)
                    canvas.drawRect(x1, y, x5, y + altoFila, paint)

                    paint.color = Color.BLACK
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 1f
                    canvas.drawRect(x1, y, x5, y + altoFila, paint)

                    canvas.drawLine(x2, y, x2, y + altoFila, paint)
                    canvas.drawLine(x3, y, x3, y + altoFila, paint)
                    canvas.drawLine(x4, y, x4, y + altoFila, paint)

                    paint.style = Paint.Style.FILL
                    paint.textSize = 12f
                    paint.typeface = Typeface.DEFAULT_BOLD
                    paint.textAlign = Paint.Align.CENTER

                    val centroY = y + 24f
                    canvas.drawText("Id", (x1 + x2) / 2, centroY, paint)
                    canvas.drawText("Nro Socio", (x2 + x3) / 2, centroY, paint)
                    canvas.drawText("Nombre y Apellido", (x3 + x4) / 2, centroY, paint)
                    canvas.drawText("Monto", (x4 + x5) / 2, centroY, paint)

                    y += altoFila

                    // FILAS CORRESPONDIENTES A LA PÁGINA ACTUAL
                    val startRow = pageIndex * rowsPerPage
                    val endRow = minOf((pageIndex + 1) * rowsPerPage, cuotas.size)

                    paint.typeface = Typeface.DEFAULT
                    paint.textSize = 12f

                    for (i in startRow until endRow) {
                        val cuota = cuotas[i]

                        paint.color = Color.rgb(239, 247, 252)
                        canvas.drawRect(x1, y, x5, y + altoFila, paint)

                        paint.color = Color.rgb(139, 200, 250)
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 1f

                        canvas.drawRect(x1, y, x5, y + altoFila, paint)
                        canvas.drawLine(x2, y, x2, y + altoFila, paint)
                        canvas.drawLine(x3, y, x3, y + altoFila, paint)
                        canvas.drawLine(x4, y, x4, y + altoFila, paint)

                        paint.style = Paint.Style.FILL
                        paint.color = Color.BLACK
                        paint.textAlign = Paint.Align.CENTER

                        val textoY = y + 24f
                        canvas.drawText(cuota[0], (x1 + x2) / 2, textoY, paint)
                        canvas.drawText(cuota[1], (x2 + x3) / 2, textoY, paint)
                        canvas.drawText(cuota[2], (x3 + x4) / 2, textoY, paint)
                        canvas.drawText(cuota[3], (x4 + x5) / 2, textoY, paint)

                        y += altoFila
                    }

                    // PIE DE PÁGINA CON NUMERACIÓN DE PÁGINAS
                    paint.textAlign = Paint.Align.LEFT
                    paint.textSize = 10f
                    paint.color = Color.GRAY
                    canvas.drawText("Reporte generado por el sistema - Fecha: $fechaHoyActual", margenIzquierdo, 800f, paint)

                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("Página ${pageIndex + 1} de $totalPages", pageWidth - margenDerecho, 800f, paint)

                    pdfDocument.finishPage(page)
                }

                pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
                pdfDocument.close()

                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
            }
        }
    }
}
