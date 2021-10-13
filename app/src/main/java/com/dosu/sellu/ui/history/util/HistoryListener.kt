package com.dosu.sellu.ui.history.util

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.util.ErrorResponse

interface HistoryListener {
    fun getSellingList(products: List<Product>, sellingList: List<Selling>)
    fun anyError(errorCode: Int?, responseBody: ErrorResponse?)
}