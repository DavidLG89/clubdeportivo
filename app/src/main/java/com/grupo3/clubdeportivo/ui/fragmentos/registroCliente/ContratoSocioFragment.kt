package com.grupo3.clubdeportivo.ui.fragmentos.registroCliente

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.registroCliente.AceptacionContratoActivity
import com.grupo3.clubdeportivo.ui.registroCliente.RegistroClienteViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ContratoSocioFragment : Fragment(R.layout.fragment_contrato_socio) {

    private val viewModel: RegistroClienteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFecha = view.findViewById<TextView>(R.id.tvFechaContrato)
        val tvParrafo1 = view.findViewById<TextView>(R.id.tvParrafo1Contrato)
        val tvParrafo2 = view.findViewById<TextView>(R.id.tvParrafo2Contrato)
        val tvParrafo3 = view.findViewById<TextView>(R.id.tvParrafo3Contrato)
        val tvParrafo4 = view.findViewById<TextView>(R.id.tvParrafo4Contrato)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaHoyStr = dateFormat.format(calendar.time)
        val diaMes = calendar.get(Calendar.DAY_OF_MONTH)

        val montoStr =
            if (viewModel.montoCuota.isNotEmpty()) "$${viewModel.montoCuota}" else "$10.000"

        val textoFecha = "Fecha: $fechaHoyStr"
        val textoP1 =
            "Se deja constancia que el cliente ${viewModel.nombre} ${viewModel.apellido}, DNI ${viewModel.dni}, ha solicitado su inscripción como socio en el Club Deportivo."
        val textoP2 =
            "El valor de la cuota mensual asciende a $montoStr.\nEl socio se compromete a pagar el monto de la cuota los días $diaMes de cada mes."
        val textoP3 =
            "Si a la fecha de vencimiento no ha cancelado la cuota, quedará inactivo y no podrá acceder a ninguna actividad hasta pagar la cuota correspondiente."
        val textoP4 = "El socio declara conocer y aceptar el reglamento interno del club."

        tvFecha?.text = textoFecha
        tvParrafo1?.text = textoP1
        tvParrafo2?.text = textoP2
        tvParrafo3?.text = textoP3
        tvParrafo4?.text = textoP4

        // Botón IMPRIMIR (Genera PDF y luego avanza a mensaje de aceptación del contrato)
        view.findViewById<Button>(R.id.btnImprimirContrato).setOnClickListener {
            val nombreSanitizado = sanitizarTexto("${viewModel.nombre}_${viewModel.apellido}")
            val nombreArchivo = "contrato_socio_${nombreSanitizado}.pdf"

            val archivoGuardado = generarGuardarPdfContrato(
                fechaStr = textoFecha,
                p1 = textoP1,
                p2 = textoP2,
                p3 = textoP3,
                p4 = textoP4,
                nombreArchivo = nombreArchivo
            )

            fun avanzarSiguientePantalla() {
                val intent = Intent(requireContext(), AceptacionContratoActivity::class.java).apply {
                    putExtra("EXTRA_NOMBRE", viewModel.nombre)
                    putExtra("EXTRA_APELLIDO", viewModel.apellido)
                    putExtra("EXTRA_DNI", viewModel.dni)
                    putExtra("EXTRA_EMAIL", viewModel.email)
                    putExtra("EXTRA_ES_SOCIO", viewModel.esSocio == true)
                    putExtra("EXTRA_APTO_FISICO", viewModel.aptoFisico)
                    putExtra("EXTRA_MONTO_CUOTA", viewModel.montoCuota)
                }
                startActivity(intent)
            }

            if (archivoGuardado != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("PDF Generado")
                    .setMessage("El contrato se guardó en la carpeta Descargas.\n\nNombre del archivo: $nombreArchivo")
                    .setPositiveButton("Aceptar") { _, _ ->
                        avanzarSiguientePantalla()
                    }
                    .setCancelable(false)
                    .show()
            } else {
                Toast.makeText(requireContext(), "No se pudo guardar el archivo PDF", Toast.LENGTH_SHORT).show()
                avanzarSiguientePantalla()
            }
        }
    }

    private fun sanitizarTexto(texto: String): String {
        val normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
        val sinAcentos = normalizado.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        val limpio = sinAcentos.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9_]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
        return if (limpio.isEmpty()) "socio" else limpio
    }

    private fun generarGuardarPdfContrato(
        fechaStr: String,
        p1: String,
        p2: String,
        p3: String,
        p4: String,
        nombreArchivo: String
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }

        val marginX = 40f
        var y = 60f
        val anchoMax = pageWidth - (marginX * 2)

        // TÍTULO: CONTRATO DE SOCIO (en negrita)
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("CONTRATO DE SOCIO", pageWidth / 2f, y, paint)

        y += 25f
        paint.strokeWidth = 1f
        canvas.drawLine(marginX, y, pageWidth - marginX, y, paint)

        y += 30f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(fechaStr, marginX, y, paint)

        y += 30f
        paint.typeface = Typeface.DEFAULT

        fun dibujarTextoWrap(texto: String) {
            val lineas = texto.split("\n")
            for (linea in lineas) {
                val palabras = linea.split(" ")
                var lineaActual = ""
                for (palabra in palabras) {
                    val test = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
                    if (paint.measureText(test) <= anchoMax) {
                        lineaActual = test
                    } else {
                        canvas.drawText(lineaActual, marginX, y, paint)
                        y += 18f
                        lineaActual = palabra
                    }
                }
                if (lineaActual.isNotEmpty()) {
                    canvas.drawText(lineaActual, marginX, y, paint)
                    y += 18f
                }
                y += 6f
            }
        }

        dibujarTextoWrap(p1)
        y += 12f
        dibujarTextoWrap(p2)
        y += 12f
        dibujarTextoWrap(p3)
        y += 16f
        canvas.drawLine(marginX, y, pageWidth - marginX, y, paint)
        y += 16f
        dibujarTextoWrap(p4)

        y += 60f
        canvas.drawLine(marginX, y, marginX + 200f, y, paint)
        y += 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Firma del Socio", marginX, y, paint)

        pdfDocument.finishPage(page)

        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val pdfFile = File(downloadsDir, nombreArchivo)
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                pdfDocument.close()
            } catch (_: Exception) {}
            null
        }
    }
}
