package com.dosu.sellu.ui.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.ui.history.util.HistoryListener
import kotlinx.coroutines.launch

class HistoryViewModel(private val sellingRepository: SellingRepository, private val productRepository: ProductRepository)
                : ViewModel() {
    private lateinit var listener: HistoryListener

    fun setListener(listener: HistoryListener){
        this.listener = listener
    }

    fun getHSellingList() = viewModelScope.launch {
        var products: List<Product>? = null
        var sellingList: List<Selling>? = null
        when(val response = productRepository.getProducts()){
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
            is NetworkResponse.Success -> products = response.value
        }
        when(val response = sellingRepository.getSellingList()){
            is NetworkResponse.Success -> sellingList = response.value
            is NetworkResponse.Failure -> listener.anyError(response.errorCode, response.errorBody)
        }
        if(products!=null && sellingList!=null) {
            listener.getSellingList(products, sellingList)
        }
    }
}