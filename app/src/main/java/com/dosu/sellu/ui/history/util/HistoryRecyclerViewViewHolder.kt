package com.dosu.sellu.ui.history.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.ui.history.model.HistorySelling
import com.dosu.sellu.util.SellU
import com.dosu.sellu.util.prize

class HistoryRecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val prize: TextView = itemView.findViewById(R.id.selling_prize)
    private val products: TextView = itemView.findViewById(R.id.products)
    private val time: TextView = itemView.findViewById(R.id.time)

    fun bind(historySelling: HistorySelling) {
        prize.text = historySelling.prize.prize
        var productsTxt = ""
        for (pMap in historySelling.products) {
            val pName =
                if (pMap.key != null) pMap.key!!.name else SellU.res.getString(R.string.unknown_product)
            productsTxt += if (pMap.value == 1) "$pName;"
            else "${pName}X${pMap.value};"
        }
        products.text = productsTxt
        time.text = historySelling.timeStr
    }
}