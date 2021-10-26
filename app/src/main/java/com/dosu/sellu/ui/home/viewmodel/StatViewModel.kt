package com.dosu.sellu.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.R
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.ui.home.util.StatListener
import com.dosu.sellu.util.SellU
import com.dosu.sellu.util.mmddyy
import kotlinx.coroutines.launch

class StatViewModel(private val productRepository: ProductRepository,
                    private val sellingRepository: SellingRepository) : ViewModel() {
    private lateinit var listener: StatListener

    fun setListener(listener: StatListener) {
        this.listener = listener
    }

    private var _products: List<Product>? = null
    private val products get() = _products!!
    private var _sellingList: List<Selling>? = null
    private val sellingList get() = _sellingList!!

    fun loadData() = viewModelScope.launch {
        when(val response = productRepository.getProducts()){
            is NetworkResponse.Success -> _products = response.value
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
        when(val response = sellingRepository.getSellingList()){
            is NetworkResponse.Success -> _sellingList = response.value
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
        if(_products!=null && _sellingList!=null) listener.dataReady()
    }

    private var stats = mutableListOf<Stat>()
    fun statistics() = viewModelScope.launch {
        stats = mutableListOf()
        var stat = Stat(sellingList[0].time.mmddyy)
        for(selling in sellingList){
            if(stat.date != selling.time.mmddyy){
                stats.add(stat)
                stat = Stat(selling.time.mmddyy)
            }
            stat.money+=selling.prize
            for(entry in selling.products) {
                products.find { it.productId == entry.key }?.let {
                    stat.outcome += it.ownPrize*entry.value
                }
                stat.count += entry.value
            }
        }
        stats.add(stat)
        listener.statsReady(stats)
        singleStat()
    }

    private var todayStat: Stat? = null
    private var allStat: Stat = Stat("all")
    private fun singleStat() = viewModelScope.launch {
        for(stat in stats){
            if(stat.date == SellU.res.getString(R.string.today)) todayStat = stat
            allStat.money += stat.money
            allStat.outcome += stat.outcome
            allStat.count += stat.count
        }
        listener.singleStat(todayStat, allStat)
    }
}

//
//fun statistics(productPos: Int) = viewModelScope.launch{
//    stats = mutableListOf()
//    var stat = Stat(sellingList[0].time.mmddyy)
//    for(selling in sellingList){
//        if(stat.date != selling.time.mmddyy){
//            stats.add(stat)
//            stat = Stat(selling.time.mmddyy)
//        }
//        val pId = products[productPos].productId
//        for(entry in selling.products) {
//            if(pId == entry.key){
//                stat.count += entry.value
//            }
//        }
//        //selling.products[pId]?.let { stat.count+=it }
//    }
//    stats.add(stat)
//    listener.statsReady(stats)
//    singleStat()
//}