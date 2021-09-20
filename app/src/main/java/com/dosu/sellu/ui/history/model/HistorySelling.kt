package com.dosu.sellu.ui.history.model

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.google.firebase.Timestamp

data class HistorySelling(
    val sellingId: String,
    val products: Map<Product?, Int>,
    val time: Timestamp,
    val prize: Double,
    val newPrize: Double?,
    val newPrizeReason: String?
){
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun Selling.toHistorySelling(allProducts: List<Product>): HistorySelling {
            val hProducts = mutableMapOf<Product?, Int>()
            products.forEach { (id, quantity) ->
                val product = allProducts.find{p -> p.productId==id}
                hProducts[product] = quantity
            }
            return HistorySelling(sellingId, hProducts, time, prize, newPrize, newPrizeReason)
        }
    }
}