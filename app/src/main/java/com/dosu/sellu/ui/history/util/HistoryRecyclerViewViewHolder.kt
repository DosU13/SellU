package com.dosu.sellu.ui.history.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.util.SellU
import com.dosu.sellu.util.hmm
import com.dosu.sellu.util.price

class HistoryRecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val prize: TextView = itemView.findViewById(R.id.selling_prize)
    private val productsTV: TextView = itemView.findViewById(R.id.products)
    private val time: TextView = itemView.findViewById(R.id.time)

    fun bind(selling: Selling, products: List<Product>) {
        prize.text = selling.prize.price
        var productsTxt = ""
        for (s in selling.products) {
            val product = products.find { it.productId == s.key }
            val pName = product?.name ?: SellU.res.getString(R.string.unknown_product)
            productsTxt += if (s.value == 1) "$pName; "
            else "${pName}X${s.value}; "
        }
        productsTV.text = productsTxt
        time.text = selling.time.hmm
    }
}