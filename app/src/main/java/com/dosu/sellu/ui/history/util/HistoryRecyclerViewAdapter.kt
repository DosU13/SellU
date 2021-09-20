package com.dosu.sellu.ui.history.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.ui.history.model.HistorySelling

class HistoryRecyclerViewAdapter(private val context: Context?)
            : RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    var sellingList: List<HistorySelling> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.fragment_selling_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = sellingList[position]
        holder.prize.text = s.prize.toString()
        var productsTxt = ""
        for(pMap in s.products){
            val pName = if(pMap.key!=null) pMap.key!!.name else context!!.getString(R.string.unknown_product)
            productsTxt += if(pMap.value==1) "$pName;"
            else "${pName}X${pMap.value};"
        }
        holder.products.text = productsTxt
        holder.time.text = s.time.toString()
    }

    override fun getItemCount(): Int {
        return sellingList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prize: TextView = itemView.findViewById(R.id.selling_prize)
        val products: TextView = itemView.findViewById(R.id.products)
        val time: TextView = itemView.findViewById(R.id.time)
        val expBtn: Button = itemView.findViewById(R.id.expand_btn)
    }
}