package com.dosu.sellu.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.ui.home.util.HomeListener
import com.dosu.sellu.util.date
import com.dosu.sellu.util.mmddyy
import kotlinx.coroutines.launch

class HomeViewModel(private val productRepository: ProductRepository,
                    private val sellingRepository: SellingRepository) : ViewModel() {
    private lateinit var listener: HomeListener

    fun setListener(listener: HomeListener) {
        this.listener = listener
    }

    private lateinit var products: List<Product>
    private lateinit var sellingList: List<Selling>

    fun loadData() {
        if(!::products.isInitialized) loadProducts()
        else if(!::sellingList.isInitialized) loadSellingList()
        else listener.dataReady()
    }

    private fun loadProducts() = viewModelScope.launch {
        when(val response = productRepository.getProducts()){
            is NetworkResponse.Success -> {
                products = response.value
                loadData()
            }
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
    }

    private fun loadSellingList() = viewModelScope.launch {
        when(val response = sellingRepository.getSellingList()){
            is NetworkResponse.Success -> {
                sellingList = response.value
                loadData()
            }
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
    }

    private val stats = mutableListOf<Stat>()
    fun statistics() = viewModelScope.launch {
        var stat = Stat(sellingList[0].time.mmddyy)
        for(selling in sellingList){
            if(stat.date != selling.time.mmddyy){
                stats.add(stat)
                stat = Stat(selling.time.mmddyy)
            }
            stat.money+=selling.prize
            for(entry in selling.products){
                val p = products.find { it.productId == entry.key }?.let {
                    stat.outcome += it.ownPrize
                }
            }
        }
        stats.add(stat)
        listener.statsReady(stats)
    }

    private var singleStat: Stat = Stat("all")
    fun singleStat() = viewModelScope.launch {
        for(stat in stats){
            singleStat.money += stat.money
            singleStat.outcome += stat.money
        }
        listener.singleStat(singleStat)
    }
}