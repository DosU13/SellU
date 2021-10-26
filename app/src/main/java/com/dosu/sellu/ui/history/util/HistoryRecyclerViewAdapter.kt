package com.dosu.sellu.ui.history.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.ui.history.util.HistoryRecyclerViewAdapter.Section.SellingSection
import com.dosu.sellu.ui.history.util.HistoryRecyclerViewAdapter.Section.HeaderSection
import com.dosu.sellu.ui.history.viewmodel.HistoryViewModel
import com.dosu.sellu.util.date

class HistoryRecyclerViewAdapter(private val context: Context?, private val viewModel: HistoryViewModel)
            : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var sellingList: List<Selling>
    private lateinit var products: List<Product>
    private var sectionList: MutableList<Section> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == HEADER_SECTION){
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_history_header, parent, false)
            HeaderViewHolder(view)
        }else {
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_history_item, parent, false)
            HistoryRecyclerViewViewHolder(viewModel.viewModelScope,  view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val section = sectionList[position]) {
            is HeaderSection -> {
                val headerVH = holder as HeaderViewHolder
                headerVH.dateTxt.text = section.headerStr}
            is SellingSection -> {
                val notHeaderVH = holder as HistoryRecyclerViewViewHolder
                notHeaderVH.bind(sellingList[section.sellingInd], products)}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(sectionList[position]){
            is HeaderSection -> HEADER_SECTION
            is SellingSection -> SELLING_SECTION
        }
    }

    override fun getItemCount(): Int {
        return sectionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSectionedList(products: List<Product>, sellingList: List<Selling>) {
        this.products = products
        this.sellingList = sellingList
        sectionList = mutableListOf()
        var headerStrIt = ""
        for((ind, s) in sellingList.withIndex()){
            if(s.time.date != headerStrIt){
                headerStrIt = s.time.date
                sectionList.add(HeaderSection(headerStrIt))
            }
            sectionList.add(SellingSection(ind))
        }
        notifyDataSetChanged()
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dateTxt: TextView = itemView.findViewById(R.id.date)
    }

    sealed class Section {
        data class HeaderSection(val headerStr: String): Section()
        data class SellingSection(val sellingInd: Int): Section()
    }

    companion object {
        const val HEADER_SECTION = 0
        const val SELLING_SECTION = 1
    }
}