package com.techullurgy.retest.components

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
fun CreatePDFTest() {

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            createPDF()
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Done")
    }
}

fun createPDF() {
    val pageWidth = 792
    val pageHeight = 1120
    val currentPageNo = 1
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNo).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    canvas.drawText(
        "Mohamed Irsath Kareem",
        canvas.width / 2f,
        canvas.height / 2f,
        Paint().apply {
            color = Color.BLACK
            textSize = 20f
        }
    )
    document.finishPage(page)
    val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Retest.pdf")
    try {
        document.writeTo(FileOutputStream(pdfFile))
    } catch (e: Exception) {
        println("Exception: $e")
    }
    document.close()
}