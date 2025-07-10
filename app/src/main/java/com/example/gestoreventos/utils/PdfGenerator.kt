package com.example.gestoreventos.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.gestoreventos.R
import com.example.gestoreventos.model.Evento
import com.example.gestoreventos.model.Usuario
import com.example.gestoreventos.model.Servicio
import com.example.gestoreventos.model.Cliente
import com.example.gestoreventos.model.Mobiliario
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.io.image.ImageDataFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator {

    companion object {
        // Colores corporativos de Caruma Barras
        private val COLOR_NEGRO = DeviceRgb(0, 0, 0)
        private val COLOR_BLANCO = DeviceRgb(255, 255, 255)
        private val COLOR_DORADO = DeviceRgb(212, 175, 55) // #d4af37
        private val COLOR_GRIS_SUAVE = DeviceRgb(245, 245, 245)

        // Función para formatear fecha con mes en español
        private fun formatearFecha(fecha: String): String {
            return try {
                val meses = mapOf(
                    "01" to "enero", "02" to "febrero", "03" to "marzo", "04" to "abril",
                    "05" to "mayo", "06" to "junio", "07" to "julio", "08" to "agosto",
                    "09" to "septiembre", "10" to "octubre", "11" to "noviembre", "12" to "diciembre"
                )

                // Asumiendo formato DD/MM/YYYY
                val partes = fecha.split("/")
                if (partes.size == 3) {
                    val dia = partes[0]
                    val mes = meses[partes[1]] ?: partes[1]
                    val año = partes[2]
                    "$dia de $mes de $año"
                } else {
                    fecha // Si no coincide el formato, devolver original
                }
            } catch (e: Exception) {
                fecha // En caso de error, devolver original
            }
        }

        fun generateClientPdf(
            context: Context,
            evento: Evento,
            cliente: Cliente?,
            servicio: Servicio?,
            empleados: List<Usuario>,
            mobiliarios: List<Mobiliario>,
            todosLosServicios: List<Servicio> = emptyList(),
            categoriasMobiliario: List<com.example.gestoreventos.model.CategoriaMobiliario> = emptyList()
        ): Uri? {
            return generatePdf(context, evento, cliente, servicio, empleados, mobiliarios, true, todosLosServicios, categoriasMobiliario)
        }

        fun generateWorkerPdf(
            context: Context,
            evento: Evento,
            cliente: Cliente?,
            servicio: Servicio?,
            empleados: List<Usuario>,
            mobiliarios: List<Mobiliario>,
            todosLosServicios: List<Servicio> = emptyList(),
            categoriasMobiliario: List<com.example.gestoreventos.model.CategoriaMobiliario> = emptyList()
        ): Uri? {
            return generatePdf(context, evento, cliente, servicio, empleados, mobiliarios, false, todosLosServicios, categoriasMobiliario)
        }

        private fun generatePdf(
            context: Context,
            evento: Evento,
            cliente: Cliente?,
            servicio: Servicio?,
            empleados: List<Usuario>,
            mobiliarios: List<Mobiliario>,
            isClientDocument: Boolean,
            todosLosServicios: List<Servicio> = emptyList(),
            categoriasMobiliario: List<com.example.gestoreventos.model.CategoriaMobiliario> = emptyList()
        ): Uri? {
            try {
                // Crear archivo temporal
                val documentType = if (isClientDocument) "Cliente" else "Trabajadores"
                val fileName = "Caruma_${documentType}_Evento_${evento.id}_${System.currentTimeMillis()}.pdf"
                val file = File(context.cacheDir, fileName)

                // Crear PDF
                val writer = PdfWriter(file)
                val pdf = PdfDocument(writer)
                val document = Document(pdf)

                // Configurar documento
                document.setMargins(40f, 40f, 40f, 40f)

                // Título principal con branding
                val companyName = Paragraph("CARUMA BARRAS PARA EVENTOS")
                    .setFontSize(18f)
                    .setBold()
                    .setFontColor(COLOR_DORADO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5f)
                document.add(companyName)

                val documentTitle = if (isClientDocument) {
                    "CONTRATO DE SERVICIO - EVENTO"
                } else {
                    "ORDEN DE TRABAJO - EVENTO"
                }

                val title = Paragraph(documentTitle)
                    .setFontSize(22f)
                    .setBold()
                    .setFontColor(COLOR_NEGRO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)
                document.add(title)

                // Contenido específico según el tipo de documento
                if (isClientDocument) {
                    addClientContent(context, document, evento, cliente, servicio, empleados, mobiliarios, todosLosServicios, categoriasMobiliario)
                } else {
                    addWorkerContent(context, document, evento, cliente, servicio, empleados, mobiliarios, todosLosServicios, categoriasMobiliario)
                }

                // Pie de página con información de Caruma Barras
                addFooter(document, isClientDocument)

                // Cerrar documento
                document.close()

                // Retornar URI del archivo
                return FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        private fun addHeader(context: Context, document: Document) {
            // Logo grande y centrado (único logo)
            try {
                val inputStream = context.resources.openRawResource(R.raw.logo_caruma)
                val logoBytes = inputStream.readBytes()
                inputStream.close()
                val logo = ImageDataFactory.create(logoBytes)
                val logoImage = Image(logo)
                logoImage.setWidth(220f)
                logoImage.setHeight(110f)
                logoImage.setHorizontalAlignment(HorizontalAlignment.CENTER)
                document.add(logoImage)
                document.add(Paragraph("").setMarginBottom(16f))
            } catch (e: Exception) {
                val placeholderText = Paragraph("CARUMA BARRAS")
                    .setFontSize(24f)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(16f)
                document.add(placeholderText)
            }
        }

        private fun addSectionTitle(document: Document, title: String) {
            // Título de sección en dorado, mayúsculas
            val sectionTitle = Paragraph(title.uppercase())
                .setFontSize(16f)
                .setBold()
                .setFontColor(COLOR_DORADO)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(18f)
                .setMarginBottom(8f)
            document.add(sectionTitle)
            // Línea dorada
            val separator = Paragraph("\u2500".repeat(40))
                .setFontColor(COLOR_DORADO)
                .setFontSize(10f)
                .setMarginBottom(10f)
            document.add(separator)
        }

        private fun addFooter(document: Document, isClientDocument: Boolean) {
            // Pie de página con fondo negro y texto blanco
            val footerBg = Paragraph("")
                .setBackgroundColor(COLOR_NEGRO)
                .setMinHeight(18f)
            document.add(footerBg)
            val companyInfo = Paragraph("CARUMA BARRAS PARA EVENTOS")
                .setFontSize(12f)
                .setBold()
                .setFontColor(COLOR_BLANCO)
                .setBackgroundColor(COLOR_NEGRO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(-18f)
            document.add(companyInfo)
            val contactInfo = Paragraph("Tel: +52 2292783543 y +52 2292783713 | Email: barrascaruma@gmail.com")
                .setFontSize(10f)
                .setFontColor(COLOR_BLANCO)
                .setBackgroundColor(COLOR_NEGRO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(0f)
            document.add(contactInfo)
        }

        private fun addStyledTable(document: Document, rows: List<Pair<String, String>>) {
            val table = Table(2).useAllAvailableWidth()
            table.setWidth(UnitValue.createPercentValue(100f))
            table.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 1f))
            var isGray = false
            for ((label, value) in rows) {
                val labelCell = com.itextpdf.layout.element.Cell().add(Paragraph(label).setBold())
                val valueCell = com.itextpdf.layout.element.Cell().add(Paragraph(value))
                if (isGray) {
                    labelCell.setBackgroundColor(COLOR_GRIS_SUAVE)
                    valueCell.setBackgroundColor(COLOR_GRIS_SUAVE)
                }
                labelCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))
                valueCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))
                table.addCell(labelCell)
                table.addCell(valueCell)
                isGray = !isGray
            }
            document.add(table)
        }

        private fun addClientContent(
            context: Context,
            document: Document,
            evento: Evento,
            cliente: Cliente?,
            servicio: Servicio?,
            empleados: List<Usuario>,
            mobiliarios: List<Mobiliario>,
            todosLosServicios: List<Servicio> = emptyList(),
            categoriasMobiliario: List<com.example.gestoreventos.model.CategoriaMobiliario> = emptyList()
        ) {
            addHeader(context, document)

            // Información del Cliente
            addSectionTitle(document, "Información del Cliente")
            addStyledTable(document, listOf(
                "Nombre del Cliente" to (cliente?.nombre ?: "No especificado"),
                "Dirección del Evento" to evento.direccionEvento
            ))

            addSectionTitle(document, "Detalles del Evento")
            addStyledTable(document, listOf(
                "Fecha" to formatearFecha(evento.fecha),
                "Horario" to "${evento.horaInicio} - ${evento.horaFin}",
                "Número de Personas" to "${evento.numeroPersonas} personas",
                "Costo Total del Evento" to "Por definir"
            ) + if (evento.detalleServicio.isNotBlank()) listOf("Comentarios Especiales" to evento.detalleServicio) else emptyList())

            // Servicios con categorías y opciones seleccionadas
            if (evento.serviciosSeleccionados.isNotEmpty()) {
                addSectionTitle(document, "Servicios Contratados")

                val serviciosTable = Table(2).useAllAvailableWidth()
                serviciosTable.setWidth(UnitValue.createPercentValue(100f))
                serviciosTable.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 1f))

                evento.serviciosSeleccionados.forEach { servicioSeleccionado ->
                    // Buscar el nombre del servicio
                    val nombreServicio = todosLosServicios.find { it.id == servicioSeleccionado.idServicio }?.nombre
                        ?: "Servicio ${servicioSeleccionado.idServicio}"

                    // Crear texto con categorías y opciones
                    val categoriasTexto = servicioSeleccionado.categoriasSeleccionadas.joinToString("\n") { categoria ->
                        "  - ${categoria.nombreCategoria}: ${categoria.opcionesSeleccionadas.joinToString(", ")}"
                    }

                    val servicioCell = com.itextpdf.layout.element.Cell().add(
                        Paragraph(nombreServicio).setBold().setFontSize(12f)
                    )
                    val detallesCell = com.itextpdf.layout.element.Cell().add(
                        Paragraph(categoriasTexto).setFontSize(11f)
                    )

                    servicioCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))
                    detallesCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))

                    serviciosTable.addCell(servicioCell)
                    serviciosTable.addCell(detallesCell)
                }

                document.add(serviciosTable)
            } else if (servicio != null) {
                // Fallback para servicios sin categorías (formato anterior)
                addSectionTitle(document, "Servicio Contratado")
                addStyledTable(document, listOf(
                    "Tipo de Servicio" to servicio.nombre,
                    "Descripción" to servicio.descripcion
                ))
            }

            addSectionTitle(document, "Información de Contacto")
            addStyledTable(document, listOf(
                "Teléfonos" to "+52 2292783543 y +52 2292783713",
                "Correo" to "barrascaruma@gmail.com"
            ))
            addSectionTitle(document, "Términos y Condiciones")
            document.add(
                Paragraph(
                    "• El servicio incluye la preparación, montaje y desmontaje del evento\n" +
                            "• El personal llegará 30 minutos antes del horario acordado\n" +
                            "• Se requiere acceso al lugar 1 hora antes del evento\n" +
                            "• Cualquier cambio debe ser comunicado con 24 horas de anticipación\n" +
                            "• El pago debe realizarse según los términos acordados"
                ).setFontSize(11f).setMarginBottom(20f)
            )
        }

        private fun addWorkerContent(
            context: Context,
            document: Document,
            evento: Evento,
            cliente: Cliente?,
            servicio: Servicio?,
            empleados: List<Usuario>,
            mobiliarios: List<Mobiliario>,
            todosLosServicios: List<Servicio> = emptyList(),
            categoriasMobiliario: List<com.example.gestoreventos.model.CategoriaMobiliario> = emptyList()
        ) {
            addHeader(context, document)

            // Información del Cliente
            addSectionTitle(document, "Información del Cliente")
            addStyledTable(document, listOf(
                "Nombre del Cliente" to (cliente?.nombre ?: "No especificado"),
                "Dirección del Evento" to evento.direccionEvento
            ))

            addSectionTitle(document, "Información del Evento")
            addStyledTable(document, listOf(
                "ID del Evento" to evento.id,
                "Fecha" to formatearFecha(evento.fecha),
                "Horario de Trabajo" to "${evento.horaInicio} - ${evento.horaFin}",
                "Llegada Requerida" to "30 minutos antes",
                "Capacidad" to "${evento.numeroPersonas} personas"
            ) + if (evento.detalleServicio.isNotBlank()) listOf("Notas Especiales" to evento.detalleServicio) else emptyList())

            // Servicios con categorías y opciones seleccionadas
            if (evento.serviciosSeleccionados.isNotEmpty()) {
                addSectionTitle(document, "Servicios a Realizar")

                val serviciosTable = Table(2).useAllAvailableWidth()
                serviciosTable.setWidth(UnitValue.createPercentValue(100f))
                serviciosTable.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 1f))

                evento.serviciosSeleccionados.forEach { servicioSeleccionado ->
                    // Buscar el nombre del servicio
                    val nombreServicio = todosLosServicios.find { it.id == servicioSeleccionado.idServicio }?.nombre
                        ?: "Servicio ${servicioSeleccionado.idServicio}"

                    // Crear texto con categorías y opciones
                    val categoriasTexto = servicioSeleccionado.categoriasSeleccionadas.joinToString("\n") { categoria ->
                        "  - ${categoria.nombreCategoria}: ${categoria.opcionesSeleccionadas.joinToString(", ")}"
                    }

                    val servicioCell = com.itextpdf.layout.element.Cell().add(
                        Paragraph(nombreServicio).setBold().setFontSize(12f)
                    )
                    val detallesCell = com.itextpdf.layout.element.Cell().add(
                        Paragraph(categoriasTexto).setFontSize(11f)
                    )

                    servicioCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))
                    detallesCell.setBorder(com.itextpdf.layout.borders.SolidBorder(COLOR_DORADO, 0.5f))

                    serviciosTable.addCell(servicioCell)
                    serviciosTable.addCell(detallesCell)
                }

                document.add(serviciosTable)
            } else if (servicio != null) {
                // Fallback para servicios sin categorías (formato anterior)
                addSectionTitle(document, "Servicio a Realizar")
                addStyledTable(document, listOf(
                    "Tipo de Servicio" to servicio.nombre,
                    "Descripción" to servicio.descripcion
                ))
            }

            if (empleados.isNotEmpty()) {
                addSectionTitle(document, "Equipo de Trabajo (${empleados.size} personas)")
                addStyledTable(document, empleados.map {
                    "Compañero" to "${it.nombre} ${it.apellidoPaterno} ${it.apellidoMaterno}"
                })
            }
            if (mobiliarios.isNotEmpty()) {
                addSectionTitle(document, "Mobiliario a Transportar (${mobiliarios.size} items)")
                addStyledTable(document, mobiliarios.map {
                    val categoria = categoriasMobiliario.find { cat -> cat.id == it.idCategoria }
                    val nombreCategoria = categoria?.nombre ?: "Sin categoría"
                    "Item" to "$nombreCategoria - Color: ${it.color}"
                })
            }
            addSectionTitle(document, "Información de Contacto")
            addStyledTable(document, listOf(
                "Teléfonos" to "+52 2292783543 y +52 2292783713",
                "Correo" to "barrascaruma@gmail.com"
            ))
            addSectionTitle(document, "Checklist de Preparación")
            document.add(
                Paragraph(
                    "□ Verificar equipamiento completo\n" +
                            "□ Confirmar horario de llegada\n" +
                            "□ Revisar ruta al lugar del evento\n" +
                            "□ Cargar teléfono completamente\n" +
                            "□ Llevar uniforme de Caruma Barras\n" +
                            "□ Verificar lista de mobiliario\n" +
                            "□ Contactar supervisor si hay dudas"
                ).setFontSize(11f).setMarginBottom(20f)
            )
        }

        fun sharePdf(context: Context, pdfUri: Uri, fileName: String) {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    putExtra(Intent.EXTRA_SUBJECT, "Reporte de Evento - Caruma Barras")
                    putExtra(Intent.EXTRA_TEXT, "Adjunto el reporte del evento: $fileName")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(intent, "Compartir PDF")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}