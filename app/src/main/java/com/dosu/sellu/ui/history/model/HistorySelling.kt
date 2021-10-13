package com.dosu.sellu.ui.history.model

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.util.date
import com.dosu.sellu.util.hmm
import java.util.*

data class HistorySellin(
    val sellingId: String,
    val products: Map<Product?, Int>,
    val dayComparator: Long,
    val timeInMillis: Long,
    val dateStr: String,
    val timeStr: String,
    val prize: Double,
    val newPrize: Double?,
    val newPrizeReason: String?
){
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun Selling.toHistorySelling(allProducts: List<Product>): HistorySellin {
            val hProducts = mutableMapOf<Product?, Int>()
            products.forEach { (id, quantity) ->
                val product = allProducts.find{p -> p.productId==id}
                hProducts[product] = quantity
            }
            val cal = Calendar.getInstance()
            cal.time = time.toDate()
            val dayComparator: Long = 512L*(cal.get(Calendar.YEAR)-1970) + cal.get(Calendar.DAY_OF_YEAR)
            val timeInMillis = cal.timeInMillis
            return HistorySellin(sellingId, hProducts, dayComparator, timeInMillis, time.date, time.hmm, prize, newPrize, newPrizeReason)
        }
    }
}