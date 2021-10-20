package com.dosu.sellu.ui.products.util

import android.net.Uri
import com.dosu.sellu.util.ErrorResponse

interface ImageListener {
    fun downloadImage(byteArray: ByteArray, productId: String, imagePos: Int)
    fun imageUri(uri: Uri?, productId: String, imagePos: Int)
    fun downloadImages(byteArrays: Array<ByteArray>, productPos: Int)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}