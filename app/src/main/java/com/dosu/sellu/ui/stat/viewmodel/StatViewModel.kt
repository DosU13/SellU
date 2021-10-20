package com.dosu.sellu.ui.stat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.ui.stat.model.Stat
import com.dosu.sellu.ui.stat.util.StatListener
import com.dosu.sellu.util.mmddyy
import kotlinx.coroutines.Dispatchers
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
    fun statistics() = viewModelScope.launch() {
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
                    stat.outcome += it.ownPrize
                }
            }
        }
        stats.add(stat)
        listener.statsReady(stats)
        singleStat()
    }

    fun statistics(productPos: Int) = viewModelScope.launch{
        stats = mutableListOf()
        var stat = Stat(sellingList[0].time.mmddyy)
        for(selling in sellingList){
            if(stat.date != selling.time.mmddyy){
                stats.add(stat)
                stat = Stat(selling.time.mmddyy)
            }
            val pId = products[productPos].productId
            for(entry in selling.products) {
                if(pId == entry.key){
                    stat.count += entry.value
                }
            }
            //selling.products[pId]?.let { stat.count+=it }
        }
        stats.add(stat)
        listener.statsReady(stats)
        singleStat()
    }

    private var singleStat: Stat = Stat("all")
    private fun singleStat() = viewModelScope.launch() {
        for(stat in stats){
            singleStat.money += stat.money
            singleStat.outcome += stat.money
        }
        listener.singleStat(singleStat)
    }
}