package com.main.mainproject.models

import android.graphics.Bitmap
import android.net.Uri

class ModelPdfView(
    var pdfUri:Uri,
    var pageNumber:Int,
    var pageCount:Int,
    var bitmap: Bitmap
)