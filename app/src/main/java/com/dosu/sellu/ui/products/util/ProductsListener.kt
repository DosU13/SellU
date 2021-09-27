package com.dosu.sellu.ui.products.util

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.util.ErrorResponse

interface ProductsListener {
    fun getProducts(products: List<Product>)
    fun updateProductSucceed()
    fun anyError(code: Int?, responseBody: ErrorResponse?)
}