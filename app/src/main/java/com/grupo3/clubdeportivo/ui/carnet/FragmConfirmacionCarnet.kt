package com.grupo3.clubdeportivo.ui.carnet

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import java.io.FileOutputStream

class FragmConfirmacionCarnet : Fragment(R.layout.fragment_confirmacion_carnet) {

    private val viewModel: CarnetViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDni = view.findViewById<TextView>(R.id.tvPrintDni)
        val tvNombre = view.findViewById<TextView>(R.id.tvPrintNombre)
        val tvNumeroSocio = view.findViewById<TextView>(R.id.tvPrintNumeroSocio)
        val tvEstadoSocio = view.findViewById<TextView>(R.id.tvPrintEstadoSocio)
        val tvAptoFisico = view.findViewById<TextView>(R.id.tvPrintAptoFisico)

        tvDni?.text = viewModel.dni
        tvNombre?.text = viewModel.nombre
        tvNumeroSocio?.text = viewModel.numeroSocio
        tvEstadoSocio?.text = viewModel.estadoSocio
        tvAptoFisico?.text = viewModel.aptoFisico

        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnImprimir = view.findViewById<Button>(R.id.btnImprimir)

        btnVolver?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnImprimir?.setOnClickListener {
            imprimirCarnet()
        }
    }

    private fun imprimirCarnet() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val nombreDocumento = "Carnet_Socio_${viewModel.}"

        printManager.print(
            nombreDocumento,
            CarnetPrintAdapter(),
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        )
    }

    private inner class CarnetPrintAdapter : PrintDocumentAdapter() {

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

            val info = PrintDocumentInfo.Builder("carnet_socio_${viewModel.dni}.pdf")
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

                // Margenes, dimensiones y esquinas redondeadas
                val xLeft = 60f
                val cardWidth = 475f
                val cardHeight = 280f
                val yTop = 100f
                val cornerRadius = 16f

                // Path recortado para esquinas redondeadas
                val cardPath = Path().apply {
                    addRoundRect(
                        xLeft, yTop, xLeft + cardWidth, yTop + cardHeight,
                        cornerRadius, cornerRadius,
                        Path.Direction.CW
                    )
                }

                canvas.save()
                canvas.clipPath(cardPath)

                // Fondo blanco del carnet
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawRect(xLeft, yTop, xLeft + cardWidth, yTop + cardHeight, paint)

                // Cabecera azul del carnet
                paint.color = Color.rgb(8, 64, 129)
                canvas.drawRect(xLeft, yTop, xLeft + cardWidth, yTop + 50f, paint)

                // Texto Cabecera
                paint.color = Color.WHITE
                paint.textSize = 18f
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("CLUB DEPORTIVO - CARNET DE SOCIO", xLeft + (cardWidth / 2f), yTop + 32f, paint)

                canvas.restore()

                // Borde exterior redondeado del carnet
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2f
                paint.color = Color.rgb(8, 64, 129)
                canvas.drawRoundRect(xLeft, yTop, xLeft + cardWidth, yTop + cardHeight, cornerRadius, cornerRadius, paint)

                // Contenido del carnet
                paint.style = Paint.Style.FILL
                paint.color = Color.BLACK
                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f

                var yText = yTop + 85f
                val xLabel = xLeft + 30f
                val xValue = xLeft + 180f

                fun dibujarCampo(etiqueta: String, valor: String) {
                    paint.typeface = Typeface.DEFAULT_BOLD
                    canvas.drawText(etiqueta, xLabel, yText, paint)
                    paint.typeface = Typeface.DEFAULT
                    canvas.drawText(valor, xValue, yText, paint)
                    yText += 32f
                }

                dibujarCampo("Nro carnet:", viewModel.numeroSocio)
                dibujarCampo("DNI:", viewModel.dni)
                dibujarCampo("Nombre:", viewModel.nombre)
                dibujarCampo("Estado:", viewModel.estadoSocio)
                dibujarCampo("Apto fisico:", viewModel.aptoFisico)

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
