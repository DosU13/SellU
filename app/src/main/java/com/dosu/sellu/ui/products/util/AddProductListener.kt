package com.dosu.sellu.ui.products.util

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.util.ErrorResponse

interface AddProductListener {
    fun addProductSucceeded(productName: String, productId: String)
    fun getProduct(product: Product)
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}