package com.grupo3.clubdeportivo.vencimiento

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.menu_principal
import com.grupo3.clubdeportivo.ui.perfil_administrador
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.File

class VencimientoDiario : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

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

        // Referencia a la tabla
        tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        // Crear la tabla
        setupTable()

        val btnImprimir = findViewById<Button>(R.id.btnImprimir)

        btnImprimir.setOnClickListener {
            imprimirCuotas()
        }

        // Navegación al Perfil del Administrador desde la barra inferior
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, perfil_administrador::class.java)
            startActivity(intent)
        }

        // Navegación al Menú Principal desde la barra inferior (Inicio)
        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, menu_principal::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP evita acumular ventanas repetidas en el historial hacia atrás
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }
    private fun setupTable() {
        // ==========================
        // ENCABEZADO DE LA TABLA
        // ==========================

        val headerRow = TableRow(this)

        val headers = listOf(
            "Id",
            "Nro Socio",
            "Nombre y Apellido",
            "Monto"
        )

        for (texto in headers) {

            val textView = TextView(this)

            textView.text = texto
            textView.setTextColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.textSize = 12f

            textView.setPadding(
                5,
                8,
                5,
                8
            )

            // Fondo del encabezado
            textView.setBackgroundResource(
                R.drawable.fondo_header_tabla
            )

            headerRow.addView(textView)
        }

        tableLayout.addView(headerRow)





        // ==========================
        // CREAR FILAS
        // ==========================

        for (cuota in cuotas) {

            val tableRow = TableRow(this)

            for (dato in cuota) {

                val textView = TextView(this)

                textView.text = dato
                textView.setTextColor(Color.BLACK)
                textView.gravity = Gravity.CENTER
                textView.textSize = 12f

                textView.setPadding(
                    5,
                    12,
                    5,
                    12
                )

                // Fondo de la celda
                textView.setBackgroundResource(
                    R.drawable.fondo_celda
                )

                tableRow.addView(textView)
            }

            tableLayout.addView(tableRow)
        }
    }

    private fun imprimirCuotas() {

        val printManager = getSystemService(PRINT_SERVICE) as PrintManager

        val nombreDocumento = "Cuotas vencidas del dia"

        printManager.print(
            nombreDocumento,
            CuotasPrintAdapter(),
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(
                    PrintAttributes.Resolution(
                        "pdf",
                        "pdf",
                        300,
                        300
                    )
                )
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        )
    }

    private inner class CuotasPrintAdapter : PrintDocumentAdapter() {

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: android.os.Bundle?
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

                val pdfDocument = android.graphics.pdf.PdfDocument()

                val pageWidth = 595
                val pageHeight = 842

                val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    1
                ).create()

                val page = pdfDocument.startPage(pageInfo)

                val canvas = page.canvas

                val paint = android.graphics.Paint()
                paint.isAntiAlias = true
                paint.color = android.graphics.Color.BLACK

                // -----------------------------------------
                // MÁRGENES
                // -----------------------------------------

                val margenIzquierdo = 40f
                val margenDerecho = 40f

                var y = 50f

                // -----------------------------------------
                // TÍTULO
                // -----------------------------------------

                paint.textSize = 22f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                paint.textAlign = android.graphics.Paint.Align.CENTER

                canvas.drawText(
                    "CLUB DEPORTIVO",
                    pageWidth / 2f,
                    y,
                    paint
                )

                y += 35f

                paint.textSize = 18f

                canvas.drawText(
                    "CUOTAS VENCIDAS DEL DÍA",
                    pageWidth / 2f,
                    y,
                    paint
                )

                y += 30f

                // -----------------------------------------
                // LÍNEA
                // -----------------------------------------

                paint.strokeWidth = 1f

                canvas.drawLine(
                    margenIzquierdo,
                    y,
                    pageWidth - margenDerecho,
                    y,
                    paint
                )

                y += 30f

                // -----------------------------------------
                // TABLA
                // -----------------------------------------

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

                // -----------------------------------------
                // ENCABEZADO
                // -----------------------------------------

                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(169, 212, 244)

                canvas.drawRect(
                    x1,
                    y,
                    x5,
                    y + altoFila,
                    paint
                )

                paint.color = android.graphics.Color.BLACK
                paint.style = android.graphics.Paint.Style.STROKE
                paint.strokeWidth = 1f

                canvas.drawRect(
                    x1,
                    y,
                    x5,
                    y + altoFila,
                    paint
                )

                canvas.drawLine(x2, y, x2, y + altoFila, paint)
                canvas.drawLine(x3, y, x3, y + altoFila, paint)
                canvas.drawLine(x4, y, x4, y + altoFila, paint)

                // Texto del encabezado

                paint.style = android.graphics.Paint.Style.FILL
                paint.textSize = 12f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                paint.textAlign = android.graphics.Paint.Align.CENTER

                val centroY = y + 24f

                canvas.drawText("Id", (x1 + x2) / 2, centroY, paint)
                canvas.drawText("Nro Socio", (x2 + x3) / 2, centroY, paint)
                canvas.drawText("Nombre y Apellido", (x3 + x4) / 2, centroY, paint)
                canvas.drawText("Monto", (x4 + x5) / 2, centroY, paint)

                y += altoFila

                // -----------------------------------------
                // FILAS
                // -----------------------------------------

                paint.typeface = android.graphics.Typeface.DEFAULT
                paint.textSize = 12f

                for (cuota in cuotas) {

                    // Fondo de la fila

                    paint.color = android.graphics.Color.rgb(
                        239,
                        247,
                        252
                    )

                    canvas.drawRect(
                        x1,
                        y,
                        x5,
                        y + altoFila,
                        paint
                    )

                    // Bordes

                    paint.color = android.graphics.Color.rgb(
                        139,
                        200,
                        250
                    )

                    paint.style = android.graphics.Paint.Style.STROKE
                    paint.strokeWidth = 1f

                    canvas.drawRect(
                        x1,
                        y,
                        x5,
                        y + altoFila,
                        paint
                    )

                    canvas.drawLine(x2, y, x2, y + altoFila, paint)
                    canvas.drawLine(x3, y, x3, y + altoFila, paint)
                    canvas.drawLine(x4, y, x4, y + altoFila, paint)

                    // Texto

                    paint.style = android.graphics.Paint.Style.FILL
                    paint.color = android.graphics.Color.BLACK
                    paint.textAlign = android.graphics.Paint.Align.CENTER

                    val textoY = y + 24f

                    canvas.drawText(
                        cuota[0],
                        (x1 + x2) / 2,
                        textoY,
                        paint
                    )

                    canvas.drawText(
                        cuota[1],
                        (x2 + x3) / 2,
                        textoY,
                        paint
                    )

                    canvas.drawText(
                        cuota[2],
                        (x3 + x4) / 2,
                        textoY,
                        paint
                    )

                    canvas.drawText(
                        cuota[3],
                        (x4 + x5) / 2,
                        textoY,
                        paint
                    )

                    y += altoFila
                }

                // -----------------------------------------
                // PIE
                // -----------------------------------------

                y += 30f

                paint.textAlign = android.graphics.Paint.Align.LEFT
                paint.textSize = 10f

                canvas.drawText(
                    "Reporte generado por el sistema",
                    margenIzquierdo,
                    y,
                    paint
                )

                pdfDocument.finishPage(page)

                // -----------------------------------------
                // GUARDAR PDF
                // -----------------------------------------

                pdfDocument.writeTo(
                    FileOutputStream(destination.fileDescriptor)
                )

                pdfDocument.close()

                callback.onWriteFinished(
                    arrayOf(PageRange.ALL_PAGES)
                )

            } catch (e: Exception) {

                callback.onWriteFailed(
                    e.message
                )
            }
        }
    }
}