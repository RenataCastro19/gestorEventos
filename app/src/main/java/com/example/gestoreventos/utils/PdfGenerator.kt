package com.example.gestoreventos.utils

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.gestoreventos.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 50
    private const val LINE_SPACING = 20

    /**
     * Genera el PDF del contrato para el cliente con el formato CARUMA SNACKS BAR
     */
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
            val pdfDocument = PdfDocument()
            var pageNumber = 1

            // ==================== PÁGINA 1: HOJA DE SERVICIOS ====================
            val page1 = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
            )
            val canvas1 = page1.canvas
            var yPosition = MARGIN.toFloat()

            // Título principal
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas1.drawText(
                "CARUMA SNACKS BAR",
                PAGE_WIDTH / 2f,
                yPosition,
                titlePaint
            )
            yPosition += 40

            // Subtítulo
            val subtitlePaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas1.drawText(
                "HOJA DE SERVICIOS - CARUMA SNACKS BAR",
                PAGE_WIDTH / 2f,
                yPosition,
                subtitlePaint
            )
            yPosition += 40

            // Configurar paint para el contenido
            val normalPaint = Paint().apply {
                textSize = 12f
                textAlign = Paint.Align.LEFT
            }
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                textAlign = Paint.Align.LEFT
            }

            // Datos del cliente
            canvas1.drawText("Nombre del Cliente: ${cliente?.nombre ?: "No especificado"}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            canvas1.drawText("Teléfono de contacto: ${cliente?.telefono ?: "No especificado"}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            canvas1.drawText("Fecha del Evento: ${evento.fecha}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            canvas1.drawText("Hora de Inicio: ${evento.horaInicio} Hora de Fin: ${evento.horaFin}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            canvas1.drawText("Dirección del Evento: ${evento.direccionEvento}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING * 2

            // Tipo de servicio
            canvas1.drawText("Tipo de Servicio de barra contratado:", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING

            // Servicios seleccionados con detalles
            evento.serviciosSeleccionados.forEach { servicioSel ->
                val servicioDetalle = todosLosServicios.find { it.id == servicioSel.idServicio }
                if (servicioDetalle != null) {
                    canvas1.drawText("• ${servicioDetalle.nombre}", MARGIN.toFloat() + 20, yPosition, normalPaint)
                    yPosition += LINE_SPACING

                    // Mostrar categorías y opciones seleccionadas
                    servicioSel.categoriasSeleccionadas.forEach { categoria ->
                        if (categoria.opcionesSeleccionadas.isNotEmpty()) {
                            canvas1.drawText("${categoria.nombreCategoria}:", MARGIN.toFloat() + 40, yPosition, normalPaint)
                            yPosition += LINE_SPACING

                            categoria.opcionesSeleccionadas.forEach { opcion ->
                                canvas1.drawText("• $opcion", MARGIN.toFloat() + 60, yPosition, normalPaint)
                                yPosition += LINE_SPACING
                            }
                        }
                    }
                }
            }

            yPosition += LINE_SPACING

            // Número de personas
            canvas1.drawText("N° de personas: ${evento.numeroPersonas} Personas", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            // Información financiera
            canvas1.drawText("Precio Total: $${String.format("%.2f", evento.precioTotal)}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            canvas1.drawText("Anticipo recibido: $${String.format("%.2f", evento.anticipo)}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING

            val saldoPendiente = evento.precioTotal - evento.anticipo
            canvas1.drawText("Saldo pendiente: $${String.format("%.2f", saldoPendiente)}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING * 2

            // Observaciones
            canvas1.drawText("Observaciones adicionales:", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING

            if (evento.detalleServicio.isNotBlank()) {
                // Dividir texto largo en múltiples líneas
                val palabras = evento.detalleServicio.split(" ")
                var lineaActual = ""
                palabras.forEach { palabra ->
                    val testLine = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
                    if (normalPaint.measureText(testLine) > PAGE_WIDTH - MARGIN * 2) {
                        canvas1.drawText(lineaActual, MARGIN.toFloat(), yPosition, normalPaint)
                        yPosition += LINE_SPACING
                        lineaActual = palabra
                    } else {
                        lineaActual = testLine
                    }
                }
                if (lineaActual.isNotEmpty()) {
                    canvas1.drawText(lineaActual, MARGIN.toFloat(), yPosition, normalPaint)
                    yPosition += LINE_SPACING
                }
            } else {
                canvas1.drawText("El servicio de barra llegará 30 a 40 min antes para el montaje, y apertura", MARGIN.toFloat(), yPosition, normalPaint)
                yPosition += LINE_SPACING
                canvas1.drawText("servicio a la hora pactada.", MARGIN.toFloat(), yPosition, normalPaint)
            }

            pdfDocument.finishPage(page1)

            // ==================== PÁGINA 2: CONTRATO ====================
            val page2 = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
            )
            val canvas2 = page2.canvas
            yPosition = MARGIN.toFloat()

            // Título del contrato
            canvas2.drawText("CARUMA SNACKS BAR", PAGE_WIDTH / 2f, yPosition, titlePaint)
            yPosition += 40

            canvas2.drawText("CONTRATO DE PRESTACIÓN DE SERVICIOS", PAGE_WIDTH / 2f, yPosition, subtitlePaint)
            yPosition += 40

            // Texto del contrato
            val contratoTexto = """
CONTRATO DE PRESTACIÓN DE SERVICIOS QUE CELEBRAN POR UNA PARTE CARUMA
SNACKS BAR, REPRESENTADO EN ESTE ACTO POR SU PROPIETARIO, A QUIEN EN LO
SUCESIVO SE LE DENOMINARÁ "EL PROVEEDOR", Y POR LA OTRA PARTE EL CLIENTE
CUYO NOMBRE Y DATOS SE ESPECIFICARÁN EN LA HOJA DE SERVICIO, A QUIEN EN LO
SUCESIVO SE LE DENOMINARÁ "EL CLIENTE", AL TENOR DE LAS SIGUIENTES
CLÁUSULAS:
            """.trimIndent()

            yPosition = drawMultilineText(canvas2, contratoTexto, MARGIN.toFloat(), yPosition, normalPaint, PAGE_WIDTH - MARGIN * 2)
            yPosition += LINE_SPACING

            // CLÁUSULAS
            canvas2.drawText("CLÁUSULAS", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING * 2

            // Primera cláusula
            canvas2.drawText("PRIMERA. - Objeto del contrato.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas2,
                "El presente contrato tiene por objeto la prestación del servicio de barra de snacks y/o micheladas por parte de EL PROVEEDOR, para el evento detallado en la hoja de servicio correspondiente.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Segunda cláusula
            canvas2.drawText("SEGUNDA.- Duración del servicio.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas2,
                "El servicio tendrá una duración de 2 horas continuas, contadas a partir de la hora de inicio pactada entre las partes.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Tercera cláusula
            canvas2.drawText("TERCERA.- Anticipo.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas2,
                "EL CLIENTE deberá entregar un anticipo del 50% del total del servicio para apartar la fecha. El restante deberá liquidarse a más tardar al inicio del evento.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            pdfDocument.finishPage(page2)

            // ==================== PÁGINA 3: CONTINUACIÓN DEL CONTRATO ====================
            val page3 = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
            )
            val canvas3 = page3.canvas
            yPosition = MARGIN.toFloat()

            // Cuarta cláusula
            canvas3.drawText("CUARTA .- Reagendación.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "En caso de que EL CLIENTE necesite reagendar el evento, deberá notificarlo con al menos 5 días naturales de anticipación a la fecha originalmente pactada. El cambio estará sujeto a disponibilidad por parte de EL PROVEEDOR. Solo se permitirá una reagendación por contrato. En caso de no cumplir con este aviso o requerir un segundo cambio, se considerará como cancelación y el anticipo no será reembolsable.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Quinta cláusula
            canvas3.drawText("QUINTA. Cancelaciones.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "En caso de cancelación por parte de EL CLIENTE, el anticipo no será reembolsable bajo ninguna circunstancia.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Sexta cláusula
            canvas3.drawText("SEXTA- Conducta de los invitados.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "En caso de que algún colaborador de CARUMA SNACKS BAR sea molestado o agredido física o verbalmente por parte de los asistentes al evento, EL PROVEEDOR se reserva el derecho de retirar su personal y suspender el servicio sin obligación de reembolso.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Séptima cláusula
            canvas3.drawText("SEPTIMA- Mobiliario.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "En caso de daño, maltrato o pérdida del mobiliario proporcionado por EL PROVEEDOR, EL CLIENTE se compromete a cubrir el 100% del valor del mismo.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Novena cláusula (sin octava según el original)
            canvas3.drawText("NOVENA.- Requerimientos.", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "EL CLIENTE se compromete a proporcionar el espacio adecuado y con sombra, acceso a electricidad, y cualquier otro requerimiento previamente acordado para la correcta instalación y operación del servicio.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING

            // Décima cláusula
            canvas3.drawText("DECIMA.-Publicidad", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            yPosition = drawMultilineText(
                canvas3,
                "EL CLIENTE autoriza a EL PROVEEDOR a tomar fotografías o videos del servicio para fines promocionales, sin afectar la privacidad de los invitados. En caso de no autorizarlo, deberá indicarlo expresamente.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING * 3

            // Título final
            canvas3.drawText("CARUMA SNACKS BAR", PAGE_WIDTH / 2f, yPosition, titlePaint)
            yPosition += LINE_SPACING * 2

            // Texto de aceptación
            yPosition = drawMultilineText(
                canvas3,
                "EL CLIENTE declara haber leído, entendido y aceptado el presente contrato en todos sus términos, obligándose al cumplimiento del mismo al firmar este documento.",
                MARGIN.toFloat(),
                yPosition,
                normalPaint,
                PAGE_WIDTH - MARGIN * 2
            )
            yPosition += LINE_SPACING * 3

            // Fecha y líneas de firma
            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            canvas3.drawText("_____________________ $fechaActual  _________________________________", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING
            canvas3.drawText("EL PROVEEDOR (CARUMA)                                    EL CLIENTE", MARGIN.toFloat(), yPosition, normalPaint)

            pdfDocument.finishPage(page3)

            // Guardar el PDF
            val fileName = "Contrato_Cliente_Evento_${evento.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * PDF para trabajadores (versión simplificada)
     */
    fun generateWorkerPdf(
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
            val pdfDocument = PdfDocument()
            val page = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            )
            val canvas = page.canvas
            var yPosition = MARGIN.toFloat()

            val titlePaint = Paint().apply {
                textSize = 20f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            val normalPaint = Paint().apply {
                textSize = 12f
                textAlign = Paint.Align.LEFT
            }
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                textAlign = Paint.Align.LEFT
            }

            // Título
            canvas.drawText("CARUMA SNACKS BAR", PAGE_WIDTH / 2f, yPosition, titlePaint)
            yPosition += 30
            canvas.drawText("INFORMACIÓN DEL EVENTO - TRABAJADORES", PAGE_WIDTH / 2f, yPosition, titlePaint)
            yPosition += 40

            // Información del evento
            canvas.drawText("Evento ID: ${evento.id}", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            canvas.drawText("Fecha: ${evento.fecha}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING
            canvas.drawText("Horario: ${evento.horaInicio} - ${evento.horaFin}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING
            canvas.drawText("Dirección: ${evento.direccionEvento}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING
            canvas.drawText("Personas: ${evento.numeroPersonas}", MARGIN.toFloat(), yPosition, normalPaint)
            yPosition += LINE_SPACING * 2

            // Personal asignado
            canvas.drawText("PERSONAL ASIGNADO:", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            empleados.forEach { emp ->
                canvas.drawText("• ${emp.nombre} ${emp.apellidoPaterno} - ${emp.telefono}", MARGIN.toFloat(), yPosition, normalPaint)
                yPosition += LINE_SPACING
            }
            yPosition += LINE_SPACING

            // Mobiliario
            canvas.drawText("MOBILIARIO:", MARGIN.toFloat(), yPosition, boldPaint)
            yPosition += LINE_SPACING
            mobiliarios.forEach { mob ->
                val categoria = categoriasMobiliario.find { it.id == mob.idCategoria }
                canvas.drawText("• ${categoria?.nombre ?: "Sin categoría"} - ${mob.color}", MARGIN.toFloat(), yPosition, normalPaint)
                yPosition += LINE_SPACING
            }
            yPosition += LINE_SPACING

            // Observaciones
            if (evento.detalleServicio.isNotBlank()) {
                canvas.drawText("OBSERVACIONES:", MARGIN.toFloat(), yPosition, boldPaint)
                yPosition += LINE_SPACING
                yPosition = drawMultilineText(canvas, evento.detalleServicio, MARGIN.toFloat(), yPosition, normalPaint, PAGE_WIDTH - MARGIN * 2)
            }

            pdfDocument.finishPage(page)

            val fileName = "Trabajadores_Evento_${evento.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Función auxiliar para dibujar texto multilínea
     */
    private fun drawMultilineText(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float,
        startY: Float,
        paint: Paint,
        maxWidth: Int
    ): Float {
        var yPosition = startY
        val palabras = text.split(" ")
        var lineaActual = ""

        palabras.forEach { palabra ->
            val testLine = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
            if (paint.measureText(testLine) > maxWidth) {
                canvas.drawText(lineaActual, x, yPosition, paint)
                yPosition += LINE_SPACING
                lineaActual = palabra
            } else {
                lineaActual = testLine
            }
        }

        if (lineaActual.isNotEmpty()) {
            canvas.drawText(lineaActual, x, yPosition, paint)
            yPosition += LINE_SPACING
        }

        return yPosition
    }

    /**
     * Compartir el PDF generado - VERSIÓN CORREGIDA
     */
    fun sharePdf(context: Context, uri: Uri, fileName: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Contrato CARUMA SNACKS BAR")
                putExtra(Intent.EXTRA_TEXT, "Adjunto el contrato de servicio")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Crear el chooser con Intent.createChooser
            val chooserIntent = Intent.createChooser(intent, "Compartir PDF")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Mostrar un mensaje de error al usuario
            android.widget.Toast.makeText(
                context,
                "Error al compartir el PDF: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * NUEVA: Función alternativa para compartir específicamente por WhatsApp
     */
    fun sharePdfWhatsApp(context: Context, uri: Uri, fileName: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                `package` = "com.whatsapp" // Forzar WhatsApp
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Contrato CARUMA SNACKS BAR")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            // Si WhatsApp no está instalado o hay error, usar el selector normal
            sharePdf(context, uri, fileName)
        }
    }

    /**
     * NUEVA: Guardar PDF en descargas y notificar al usuario
     */
    fun savePdfToDownloads(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Android 10 y superior - usar MediaStore
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

                    android.widget.Toast.makeText(
                        context,
                        "PDF guardado en Descargas",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    true
                } ?: false
            } else {
                // Android 9 y anteriores
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                )
                val targetFile = File(downloadsDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Notificar al sistema que hay un nuevo archivo
                android.media.MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )

                android.widget.Toast.makeText(
                    context,
                    "PDF guardado en Descargas",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(
                context,
                "Error al guardar PDF: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
            false
        }
    }
}