package edu.ucne.ureserve.presentation.reportes

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.print.PrintManager
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun generarPdfReservas(context: Context, reservas: List<ReservacionesDto>): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Reporte_Cubiculos_$timeStamp.pdf"
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, fileName)

    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
    document.open()

    document.add(Paragraph("REPORTE DE CUBÍCULOS\n\n"))

    reservas.forEach { reserva ->
        document.add(Paragraph("No. Reserva: ${reserva.codigoReserva}"))
        document.add(Paragraph("Fecha: ${reserva.fechaFormateada}"))
        document.add(Paragraph("Horario: ${reserva.horaInicio} a ${reserva.horaFin}"))
        document.add(Paragraph("Matrícula: ${reserva.matricula}"))
        document.add(Paragraph("\n")) // Separador
    }

    document.close()
    return pdfFile
}

fun imprimirPdf(context: Context, reservas: List<ReservacionesDto>) {
    val pdfFile = generarPdfReservas(context, reservas)
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfDocumentPrintAdapter(context, Uri.fromFile(pdfFile))
    printManager.print("Reporte Cubículos", printAdapter, null)
}

fun generarPdfReservasProyectores(context: Context, reservas: List<ReservacionesDto>): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Reporte_Proyectores_$timeStamp.pdf"  // Cambiado a "Proyectores"
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, fileName)

    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
    document.open()

    document.add(Paragraph("REPORTE DE PROYECTORES\n\n"))
    document.add(Paragraph("Total reservas: ${reservas.size}\n\n"))

    reservas.forEach { reserva ->
        document.add(Paragraph("No. Reserva: ${reserva.codigoReserva}"))
        document.add(Paragraph("Fecha: ${reserva.fechaFormateada}"))
        document.add(Paragraph("Horario: ${reserva.horaInicio} a ${reserva.horaFin}"))
        document.add(Paragraph("Matrícula: ${reserva.matricula}"))
        document.add(Paragraph("--------------------------------"))  // Separador diferente
    }

    document.close()
    return pdfFile
}

fun imprimirPdfProyectores(context: Context, reservas: List<ReservacionesDto>) {
    val pdfFile = generarPdfReservasProyectores(context, reservas)
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfDocumentPrintAdapter(context, Uri.fromFile(pdfFile))
    printManager.print("Reporte Proyectores", printAdapter, null)
}

fun generarPdfReservasLaboratorios(context: Context, reservas: List<ReservacionesDto>): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Reporte_Laboratorios_$timeStamp.pdf"
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, fileName)

    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
    document.open()

    document.add(Paragraph("REPORTE DE LABORATORIOS\n\n"))
    document.add(Paragraph("Total reservas: ${reservas.size}\n\n"))
    document.add(Paragraph("Generado el: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())}\n\n"))

    reservas.forEachIndexed { index, reserva ->
        document.add(Paragraph("RESERVA #${index + 1}"))
        document.add(Paragraph("------------------------"))
        document.add(Paragraph("Código: ${reserva.codigoReserva}"))
        document.add(Paragraph("Fecha: ${reserva.fechaFormateada}"))
        document.add(Paragraph("Horario: ${reserva.horaInicio} - ${reserva.horaFin}"))
        document.add(Paragraph("Matrícula: ${reserva.matricula}"))
        document.add(Paragraph("\n"))
    }

    document.close()
    return pdfFile
}

fun imprimirPdfLaboratorios(context: Context, reservas: List<ReservacionesDto>) {
    val pdfFile = generarPdfReservasLaboratorios(context, reservas)
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfDocumentPrintAdapter(context, Uri.fromFile(pdfFile))
    printManager.print("Reporte Laboratorios", printAdapter, null)
}

fun generarPdfReservasRestaurantes(context: Context, reservas: List<ReservacionesDto>): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Reporte_Laboratorios_$timeStamp.pdf"
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, fileName)

    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
    document.open()

    document.add(Paragraph("REPORTE DE RESTAURANTES\n\n"))
    document.add(Paragraph("Total reservas: ${reservas.size}\n\n"))
    document.add(Paragraph("Generado el: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())}\n\n"))

    reservas.forEachIndexed { index, reserva ->
        document.add(Paragraph("RESERVA #${index + 1}"))
        document.add(Paragraph("------------------------"))
        document.add(Paragraph("Código: ${reserva.codigoReserva}"))
        document.add(Paragraph("Fecha: ${reserva.fechaFormateada}"))
        document.add(Paragraph("Horario: ${reserva.horaInicio} - ${reserva.horaFin}"))
        document.add(Paragraph("Matrícula: ${reserva.matricula}"))
        document.add(Paragraph("\n"))
    }

    document.close()
    return pdfFile
}

fun imprimirPdfRestaurantes(context: Context, reservas: List<ReservacionesDto>) {
    val pdfFile = generarPdfReservasRestaurantes(context, reservas)
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfDocumentPrintAdapter(context, Uri.fromFile(pdfFile))
    printManager.print("Reporte Restaurantes", printAdapter, null)
}

fun generarPdfReservasGeneral(context: Context, reservas: List<ReservacionesDto>): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Reporte_Laboratorios_$timeStamp.pdf"
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, fileName)

    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
    document.open()

    document.add(Paragraph("REPORTE GENERAL\n\n"))
    document.add(Paragraph("Total reservas: ${reservas.size}\n\n"))
    document.add(Paragraph("Generado el: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())}\n\n"))

    reservas.forEachIndexed { index, reserva ->
        document.add(Paragraph("RESERVA #${index + 1}"))
        document.add(Paragraph("------------------------"))
        document.add(Paragraph("Código: ${reserva.codigoReserva}"))
        document.add(Paragraph("Fecha: ${reserva.fechaFormateada}"))
        document.add(Paragraph("Horario: ${reserva.horaInicio} - ${reserva.horaFin}"))
        document.add(Paragraph("Matrícula: ${reserva.matricula}"))
        document.add(Paragraph("\n"))
    }

    document.close()
    return pdfFile
}

fun imprimirPdfGeneral(context: Context, reservas: List<ReservacionesDto>) {
    val pdfFile = generarPdfReservasLaboratorios(context, reservas)
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfDocumentPrintAdapter(context, Uri.fromFile(pdfFile))
    printManager.print("Reporte General", printAdapter, null)
}