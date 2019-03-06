package com.ajithvgiri.pdf2bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri

object PDFThumbnailUtils {
    fun convertPDFtoBitmap(context: Context, uri: Uri, pageNumber: Int): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val currentPage = pdfRenderer.openPage(pageNumber)
        val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.RGB_565)
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        // Here, we render the page onto the Bitmap.
        return bitmap
    }
}