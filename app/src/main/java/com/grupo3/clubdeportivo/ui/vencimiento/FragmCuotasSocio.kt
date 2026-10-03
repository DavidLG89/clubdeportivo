package com.grupo3.clubdeportivo.ui.vencimiento

import android.content.Context
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
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.MenuPrincipalActivity
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmCuotasSocio : Fragment(R.layout.fragment_cuotas_socio) {

    private val viewModel: VencimientoDniViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvInfoSocio = view.findViewById<TextView>(R.id.tvInfoSocio)
        val tvDniSocio = view.findViewById<TextView>(R.id.tvDniSocio)
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayoutCuotas)

        tvInfoSocio?.text = "SOCIO: ${viewModel.nombreSocio} (${viewModel.numeroSocio})"
        tvDniSocio?.text = "DNI: ${viewModel.dni}"

        setupTable(tableLayout)

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnImprimir = view.findViewById<Button>(R.id.btnImprimir)

        btnVolver?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnImprimir?.setOnClickListener {
            imprimirCuotasSocio()
        }
    }

    private fun setupTable(tableLayout: TableLayout?) {
        if (tableLayout == null) return

        tableLayout.removeAllViews()

        // Encabezados
        val headerRow = TableRow(requireContext())
        val headers = listOf("Id", "Vencimiento", "Monto", "Estado")

        for (texto in headers) {
            val textView = TextView(requireContext()).apply {
                text = texto
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                textSize = 12f
                setPadding(4, 8, 4, 8)
                setBackgroundResource(R.drawable.fondo_header_tabla)
            }
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        // Filas de datos
        for (cuota in viewModel.cuotasSocio) {
            val tableRow = TableRow(requireContext())
            for (dato in cuota) {
                val textView = TextView(requireContext()).apply {
                    text = dato
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                    textSize = 11f
                    setPadding(4, 10, 4, 10)
                    setBackgroundResource(R.drawable.fondo_celda)
                }
                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
    }

    private fun imprimirCuotasSocio() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val nombreDocumento = "Cuotas_Socio_${viewModel.nombreSocio}"

        printManager.print(
            nombreDocumento,
            CuotasSocioPrintAdapter(),
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        )
    }

    private fun mostrarDialogoOpciones() {
        if (!isAdded) return

        AlertDialog.Builder(requireContext())
            .setTitle("Impresión finalizada")
            .setMessage("¿Desea volver al menú principal o continuar en esta pantalla?")
            .setPositiveButton("Ir al Inicio") { _, _ ->
                val intent = Intent(requireContext(), MenuPrincipalActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Continuar aquí", null)
            .show()
    }

    private inner class CuotasSocioPrintAdapter : PrintDocumentAdapter() {

        override fun onFinish() {
            super.onFinish()
            activity?.runOnUiThread {
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

            val info = PrintDocumentInfo.Builder("cuotas_socio_${viewModel.dni}.pdf")
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

                y += 32f
                paint.textSize = 18f
                canvas.drawText("ESTADO DE CUOTAS DEL SOCIO", pageWidth / 2f, y, paint)

                y += 24f
                paint.textSize = 12f
                paint.typeface = Typeface.DEFAULT
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                canvas.drawText("Fecha de emisión: $fechaActual", pageWidth / 2f, y, paint)

                y += 20f
                paint.strokeWidth = 1f
                canvas.drawLine(margenIzquierdo, y, pageWidth - margenDerecho, y, paint)

                y += 25f
                // DATOS DEL SOCIO
                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 13f
                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText("Socio: ${viewModel.nombreSocio} (Nº ${viewModel.numeroSocio})", margenIzquierdo, y, paint)
                y += 20f
                canvas.drawText("DNI: ${viewModel.dni}", margenIzquierdo, y, paint)

                y += 25f

                // TABLA DE CUOTAS PDF
                val anchoTabla = pageWidth - margenIzquierdo - margenDerecho
                val anchoId = 55f
                val anchoVencimiento = 130f
                val anchoMonto = 140f
                val anchoEstado = anchoTabla - anchoId - anchoVencimiento - anchoMonto
                val altoFila = 36f

                val x1 = margenIzquierdo
                val x2 = x1 + anchoId
                val x3 = x2 + anchoVencimiento
                val x4 = x3 + anchoMonto
                val x5 = x4 + anchoEstado

                // ENCABEZADOS DE TABLA PDF
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

                val centroY = y + 23f
                canvas.drawText("Id", (x1 + x2) / 2, centroY, paint)
                canvas.drawText("Vencimiento", (x2 + x3) / 2, centroY, paint)
                canvas.drawText("Monto", (x3 + x4) / 2, centroY, paint)
                canvas.drawText("Estado", (x4 + x5) / 2, centroY, paint)

                y += altoFila

                // FILAS DE DATOS EN PDF
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 12f

                for (cuota in viewModel.cuotasSocio) {
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

                    val textoY = y + 23f
                    canvas.drawText(cuota[0], (x1 + x2) / 2, textoY, paint)
                    canvas.drawText(cuota[1], (x2 + x3) / 2, textoY, paint)
                    canvas.drawText(cuota[2], (x3 + x4) / 2, textoY, paint)
                    canvas.drawText(cuota[3], (x4 + x5) / 2, textoY, paint)

                    y += altoFila
                }

                // PIE DE PÁGINA
                y += 30f
                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 10f
                paint.color = Color.GRAY
                canvas.drawText("Reporte de cuotas generado por el sistema - Club Deportivo", margenIzquierdo, y, paint)

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
