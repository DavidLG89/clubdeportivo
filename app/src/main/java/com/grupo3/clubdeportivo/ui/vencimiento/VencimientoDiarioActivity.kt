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
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import com.grupo3.clubdeportivo.ui.PerfilAdminActivity
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VencimientoDiarioActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var fechaHoyActual: String

    // ==========================
    // DATOS DE PRUEBA
    // ==========================

    val cuotas: List<List<String>> = listOf(
        listOf("1", "14", "Roberto Gomez", "$30.000"),
        listOf("22", "55", "Juan Amarilla", "$30.000"),
        listOf("13", "10", "David Sanchez", "$30.000"),
        listOf("4", "3", "Sergio Gon", "$30.000"),
        listOf("60", "17", "Pablo Leguizamon", "$30.000"),
        listOf("45", "4", "Maximo Perez", "$30.000"),
        listOf("8", "78", "Delfina Cabañas", "$30.000")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vencimiento_diario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Obtener fecha actual dinámicamente
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        fechaHoyActual = sdf.format(Date())

        val tvFechaHoy = findViewById<TextView>(R.id.tvFechaHoy)
        tvFechaHoy.text = "Fecha: $fechaHoyActual"

        // Referencia a la tabla
        tableLayout = findViewById(R.id.tableLayout)

        // Crear la tabla
        setupTable()

        val btnImprimir = findViewById<Button>(R.id.btnImprimir)

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

    private fun setupTable() {
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

    private inner class CuotasPrintAdapter : PrintDocumentAdapter() {

        override fun onFinish() {
            super.onFinish()
            runOnUiThread {
                mostrarDialogoOpciones()
            }
        }

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

            val info = PrintDocumentInfo.Builder("cuotas_vencidas_del_dia.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build()

            callback.onLayoutFinished(info, true)
        }

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

                val pageInfo = PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    1
                ).create()

                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val paint = Paint().apply {
                    isAntiAlias = true
                    color = Color.BLACK
                }

                val margenIzquierdo = 40f
                val margenDerecho = 40f
                var y = 50f

                // TÍTULO
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

                // TABLA
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

                // ENCABEZADO PDF
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

                // FILAS DE DATOS EN PDF
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 12f

                for (cuota in cuotas) {
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

                // PIE DE PÁGINA PDF
                y += 30f
                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 10f
                canvas.drawText("Reporte generado por el sistema - Fecha: $fechaHoyActual", margenIzquierdo, y, paint)

                pdfDocument.finishPage(page)

                pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
                pdfDocument.close()

                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
            }
        }
    }
}
