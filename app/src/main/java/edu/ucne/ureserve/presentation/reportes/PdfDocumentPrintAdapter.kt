package edu.ucne.ureserve.presentation.reportes

import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream

class PdfDocumentPrintAdapter(private val context: Context, private val pdfUri: Uri) : PrintDocumentAdapter() {

    private var pdfFileDescriptor: ParcelFileDescriptor? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: android.os.Bundle?
    ) {
        try {
            pdfFileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r")
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }
            val pdi = PrintDocumentInfo.Builder("reporte_cubiculos.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            callback.onLayoutFinished(pdi, true)
        } catch (e: Exception) {
            callback.onLayoutFailed(e.message)
        }
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        try {
            val input = FileInputStream(pdfFileDescriptor?.fileDescriptor)
            val output = FileOutputStream(destination.fileDescriptor)

            val buf = ByteArray(1024)
            var bytesRead: Int

            while (input.read(buf).also { bytesRead = it } > 0) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    input.close()
                    output.close()
                    return
                }
                output.write(buf, 0, bytesRead)
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            input.close()
            output.close()
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        }
    }

    override fun onFinish() {
        try {
            pdfFileDescriptor?.close()
        } catch (e: Exception) {
            Log.e("PdfDocumentPrintAdapter", "Error closing file descriptor", e)
        }
    }
}