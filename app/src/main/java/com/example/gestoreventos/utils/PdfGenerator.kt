package com.example.gestoreventos.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.gestoreventos.R
import com.example.gestoreventos.model.*
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    private fun formatearFechaParaNombre(fecha: String): String {
        return try {
            val meses = arrayOf(
                "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            )
            val partes = fecha.split("/")
            val dia = partes[0]
            val mes = meses[partes[1].toInt()]
            val anio = partes[2]
            "${dia}${mes}${anio}"
        } catch (e: Exception) {
            "Evento"
        }
    }

    private class FooterEventHandler(private val fuente: PdfFont) : IEventHandler {
        override fun handleEvent(event: Event) {
            val docEvent = event as PdfDocumentEvent
            val page = docEvent.page
            val pageSize = page.pageSize
            val canvas = PdfCanvas(page)
            canvas.beginText()
                .setFontAndSize(fuente, 9f)
                .moveText((pageSize.right - 130).toDouble(), 25.0)
                .showText("CARUMA SNACKS BAR")
                .endText()
            canvas.release()
        }
    }

    private fun addLogo(context: Context, document: Document) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.logo_caruma)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageData = ImageDataFactory.create(stream.toByteArray())
            val image = Image(imageData).setWidth(90f).setMarginBottom(15f)
            document.add(image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addCampo(document: Document, boldFont: PdfFont, normalFont: PdfFont, etiqueta: String, valor: String) {
        document.add(
            Paragraph()
                .add(Text("$etiqueta: ").setFont(boldFont).setFontSize(11f))
                .add(Text(valor).setFont(normalFont).setFontSize(11f))
                .setMarginBottom(6f)
        )
    }

    private fun celdaSinBorde(texto: String, font: PdfFont): Cell {
        return Cell()
            .add(Paragraph(texto).setFont(font).setFontSize(10.5f))
            .setBorder(Border.NO_BORDER)
    }

    fun generateClientPdf(
        context: Context,
        evento: Evento,
        cliente: Cliente?,
        servicio: Servicio?,
        empleados: List<Usuario>,
        mobiliarios: List<Mobiliario>,
        todosLosServicios: List<Servicio>,
        categoriasMobiliario: List<CategoriaMobiliario>
    ): Uri? {
        return try {
            val fechaFormateada = formatearFechaParaNombre(evento.fecha)
            val fileName = "Contrato_$fechaFormateada.pdf"
            val file = File(context.cacheDir, fileName)

            val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

            val writer = PdfWriter(FileOutputStream(file))
            val pdfDoc = PdfDocument(writer)
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, FooterEventHandler(normalFont))
            val document = Document(pdfDoc)
            document.setMargins(50f, 50f, 60f, 50f)

            addLogo(context, document)

            document.add(
                Paragraph("HOJA DE SERVICIOS - CARUMA SNACKS BAR")
                    .setFont(boldFont)
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)
            )

            addCampo(document, boldFont, normalFont, "Nombre del Cliente", cliente?.nombre ?: "No especificado")
            addCampo(document, boldFont, normalFont, "Teléfono de contacto", cliente?.telefono ?: "No especificado")
            addCampo(document, boldFont, normalFont, "Fecha del Evento", evento.fecha)
            addCampo(document, boldFont, normalFont, "Hora de Inicio - Hora de Fin", "${evento.horaInicio} - ${evento.horaFin}")
            addCampo(document, boldFont, normalFont, "Dirección del Evento", evento.direccionEvento)
            addCampo(document, boldFont, normalFont, "N° de personas", "${evento.numeroPersonas} personas")

            document.add(
                Paragraph("Tipo de Servicio de barra contratado:")
                    .setFont(boldFont)
                    .setFontSize(12f)
                    .setMarginTop(10f)
            )

            evento.serviciosSeleccionados.forEach { servicioSel ->
                val nombreServicio = todosLosServicios.find { it.id == servicioSel.idServicio }?.nombre
                    ?: "Servicio ${servicioSel.idServicio}"

                document.add(
                    Paragraph("• $nombreServicio (${servicioSel.cantidad} pz)")
                        .setFont(normalFont)
                        .setFontSize(11f)
                        .setMarginLeft(12f)
                        .setMarginBottom(2f)
                )

                servicioSel.categoriasSeleccionadas.forEach { categoria ->
                    if (categoria.opcionesSeleccionadas.isNotEmpty()) {
                        document.add(
                            Paragraph("${categoria.nombreCategoria}:")
                                .setFont(boldFont)
                                .setFontSize(11f)
                                .setMarginLeft(15f)
                                .setMarginBottom(2f)
                        )
                        categoria.opcionesSeleccionadas.forEach { opcion ->
                            document.add(
                                Paragraph("• $opcion")
                                    .setFont(normalFont)
                                    .setFontSize(11f)
                                    .setMarginLeft(25f)
                                    .setMarginBottom(2f)
                            )
                        }
                    }
                }
            }

            document.add(Paragraph(" ").setMarginTop(5f))

            addCampo(document, boldFont, normalFont, "Precio Total", "$${String.format("%.2f", evento.precioTotal)}")
            addCampo(document, boldFont, normalFont, "Anticipo recibido", "$${String.format("%.2f", evento.anticipo)}")
            val saldoPendiente = evento.precioTotal - evento.anticipo
            addCampo(document, boldFont, normalFont, "Saldo pendiente", "$${String.format("%.2f", saldoPendiente)}")

            document.add(
                Paragraph("Observaciones adicionales:")
                    .setFont(boldFont)
                    .setFontSize(12f)
                    .setMarginTop(15f)
            )
            val observaciones = evento.detalleServicio.ifBlank {
                "El servicio de barra llegará 30 a 40 min antes para el montaje, y apertura servicio a la hora pactada."
            }
            document.add(Paragraph(observaciones).setFont(normalFont).setFontSize(11f))

            document.add(AreaBreak())

            addLogo(context, document)
            document.add(
                Paragraph("CONTRATO DE PRESTACIÓN DE SERVICIOS")
                    .setFont(boldFont)
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15f)
            )

            val introContrato = "CONTRATO DE PRESTACIÓN DE SERVICIOS QUE CELEBRAN POR UNA PARTE CARUMA SNACKS BAR, " +
                    "REPRESENTADO EN ESTE ACTO POR SU PROPIETARIO, A QUIEN EN LO SUCESIVO SE LE DENOMINARÁ \"EL PROVEEDOR\", " +
                    "Y POR LA OTRA PARTE EL CLIENTE CUYO NOMBRE Y DATOS SE ESPECIFICARÁN EN LA HOJA DE SERVICIO, A QUIEN EN LO " +
                    "SUCESIVO SE LE DENOMINARÁ \"EL CLIENTE\", AL TENOR DE LAS SIGUIENTES CLÁUSULAS:"
            document.add(
                Paragraph(introContrato)
                    .setFont(normalFont)
                    .setFontSize(10.5f)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
            )

            document.add(
                Paragraph("CLÁUSULAS")
                    .setFont(boldFont)
                    .setFontSize(12f)
                    .setMarginTop(15f)
                    .setMarginBottom(10f)
            )

            val clausulas = listOf(
                "PRIMERA. - Objeto del contrato." to "El presente contrato tiene por objeto la prestación del servicio de barra de snacks y/o micheladas por parte de EL PROVEEDOR, para el evento detallado en la hoja de servicio correspondiente.",
                "SEGUNDA.- Duración del servicio." to "El servicio tendrá una duración de 2 horas continuas, contadas a partir de la hora de inicio pactada entre las partes.",
                "TERCERA.- Anticipo." to "EL CLIENTE deberá entregar un anticipo del 50% del total del servicio para apartar la fecha. El restante deberá liquidarse a más tardar al inicio del evento.",
                "CUARTA.- Reagendación." to "En caso de que EL CLIENTE necesite reagendar el evento, deberá notificarlo con al menos 5 días naturales de anticipación a la fecha originalmente pactada. El cambio estará sujeto a disponibilidad por parte de EL PROVEEDOR. Solo se permitirá una reagendación por contrato. En caso de no cumplir con este aviso o requerir un segundo cambio, se considerará como cancelación y el anticipo no será reembolsable.",
                "QUINTA.- Cancelaciones." to "En caso de cancelación por parte de EL CLIENTE, el anticipo no será reembolsable bajo ninguna circunstancia.",
                "SEXTA.- Conducta de los invitados." to "En caso de que algún colaborador de CARUMA SNACKS BAR sea molestado o agredido física o verbalmente por parte de los asistentes al evento, EL PROVEEDOR se reserva el derecho de retirar su personal y suspender el servicio sin obligación de reembolso.",
                "SÉPTIMA.- Mobiliario." to "En caso de daño, maltrato o pérdida del mobiliario proporcionado por EL PROVEEDOR, EL CLIENTE se compromete a cubrir el 100% del valor del mismo.",
                "NOVENA.- Requerimientos." to "EL CLIENTE se compromete a proporcionar el espacio adecuado y con sombra, acceso a electricidad, y cualquier otro requerimiento previamente acordado para la correcta instalación y operación del servicio.",
                "DÉCIMA.- Publicidad." to "EL CLIENTE autoriza a EL PROVEEDOR a tomar fotografías o videos del servicio para fines promocionales, sin afectar la privacidad de los invitados. En caso de no autorizarlo, deberá indicarlo expresamente."
            )

            clausulas.forEach { (titulo, cuerpo) ->
                document.add(Paragraph(titulo).setFont(boldFont).setFontSize(11f).setMarginBottom(3f))
                document.add(
                    Paragraph(cuerpo)
                        .setFont(normalFont)
                        .setFontSize(10.5f)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setMarginBottom(10f)
                )
            }

            document.add(Paragraph(" ").setMarginTop(20f))
            document.add(
                Paragraph("EL CLIENTE declara haber leído, entendido y aceptado el presente contrato en todos sus términos, obligándose al cumplimiento del mismo al firmar este documento.")
                    .setFont(normalFont)
                    .setFontSize(10.5f)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setMarginBottom(50f)
            )

            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val tablaFirmas = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).useAllAvailableWidth()
            tablaFirmas.addCell(celdaSinBorde("_____________________  $fechaActual", normalFont))
            tablaFirmas.addCell(celdaSinBorde("_____________________", normalFont))
            tablaFirmas.addCell(celdaSinBorde("EL PROVEEDOR (CARUMA)", normalFont))
            tablaFirmas.addCell(celdaSinBorde("EL CLIENTE", normalFont))
            document.add(tablaFirmas)

            document.close()

            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sharePdf(context: Context, uri: Uri, fileName: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Contrato CARUMA SNACKS BAR")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Error al compartir: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun savePdfToDownloads(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val contentUri = resolver.insert(
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                contentUri?.let { targetUri ->
                    resolver.openOutputStream(targetUri)?.use { outputStream ->
                        resolver.openInputStream(uri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    true
                } ?: false
            } else {
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                )
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val targetFile = File(downloadsDir, fileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                android.media.MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "Error al guardar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            false
        }
    }
}