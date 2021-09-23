package com.dosu.sellu.ui.products.util

import com.dosu.sellu.util.ErrorResponse

interface ImageListener {
    fun downloadImage(byteArray: ByteArray, productId: String, imagePos: Int)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}