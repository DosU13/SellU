package com.dosu.sellu.ui.products.util

import com.dosu.sellu.util.ErrorResponse

interface AddProductListener {
    fun addProductSucceeded(productName: String, productId: String)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}