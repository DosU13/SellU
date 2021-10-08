package com.dosu.sellu.ui.history.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.ui.history.model.HistorySelling
import com.dosu.sellu.util.SellU

class HistoryRecyclerViewAdapter(private val context: Context?)
            : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var sellingList: List<HistorySelling> = emptyList()
    private var sectionList: MutableList<Section> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == HEADER){
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_history_header, parent, false)
            HeaderViewHolder(view)
        }else {
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_history_item, parent, false)
            HistoryRecyclerViewViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(sectionList[position].viewType == HEADER){
            val headerVH = holder as HeaderViewHolder
            headerVH.dateTxt.text = sectionList[position].headerStr
        }else {
            val notHeaderVH = holder as HistoryRecyclerViewViewHolder
            notHeaderVH.bind(sellingList[sectionList[position].sellingInd!!])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return sectionList[position].viewType
    }

    override fun getItemCount(): Int {
        return sectionList.size
    }

    fun updateSectionedList(newSellingList: List<HistorySelling>) {
        sellingList = newSellingList.sortedWith { s1, s2 ->
            if(s1.timeInMillis < s2.timeInMillis) 1 else -1
        }
        sectionList = mutableListOf()
        var headerStrIt = context!!.getString(R.string.today)
        for((ind, s) in sellingList.withIndex()){
            if(s.dateStr != headerStrIt){
                headerStrIt = s.dateStr
                sectionList.add(Section(HEADER, headerStrIt, null))
            }
            sectionList.add(Section(NOT_HEADER, null, ind))
        }
        notifyDataSetChanged()
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dateTxt: TextView = itemView.findViewById(R.id.date)
    }

    data class Section(val viewType: Int, val headerStr: String?, val sellingInd: Int?)

    companion object{
        private const val HEADER = 1
        private const val NOT_HEADER = 0
    }
}