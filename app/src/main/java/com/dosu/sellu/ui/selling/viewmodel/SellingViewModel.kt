package com.dosu.sellu.ui.selling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.selling.SellingRepository
import com.dosu.sellu.data.network.selling.model.SellingWithoutId
import com.dosu.sellu.ui.selling.util.AddSellingListener
import com.dosu.sellu.util.ErrorResponse
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class SellingViewModel(private val sellingRepository: SellingRepository, private val productRepository: ProductRepository) : ViewModel(){
    private lateinit var addListener: AddSellingListener

    var sellingQuantities: MutableMap<String, Int> = mutableMapOf()

    fun setListener(listener: AddSellingListener){
        addListener = listener
    }

    fun getSellingQuantity(productId: String): Int {
        return if (sellingQuantities.containsKey(productId)) sellingQuantities[productId]!!
                else 0
    }

    fun decreaseQuantity(productId: String){
        when {
            !sellingQuantities.containsKey(productId) -> sellingQuantities[productId] = 0
            sellingQuantities[productId]!! > 1 -> sellingQuantities[productId] = sellingQuantities[productId]!!-1
            sellingQuantities[productId]!! == 1 -> sellingQuantities.remove(productId)
        }
    }

    fun increaseQuantity(productId: String){
        if(!sellingQuantities.containsKey(productId)) sellingQuantities[productId] = 1
        else sellingQuantities[productId] = sellingQuantities[productId]!!+1
    }

    fun sell(prize: Double, newPrize: Double?, newPrizeReason: String?){
        val sellingMap = sellingQuantities.toMap()
        if(sellingMap.isEmpty()){
            if(::addListener.isInitialized){
                addListener.anyError(null, ErrorResponse("choose a products to sell"))
            }
        }else {
            val time: Timestamp = Timestamp.now()
            val selling = SellingWithoutId(sellingMap, time, prize, newPrize, newPrizeReason)
            addSelling(selling)
        }
    }

    fun getSummaryPrize() = viewModelScope.launch{
        when(val response = productRepository.getProducts()){
            is NetworkResponse.Success -> {
                val products = response.value
                var summaryPrize = 0.0
                for(q in sellingQuantities){
                    products.find { it.productId == q.key }?.also {p: Product -> summaryPrize += p.prize*q.value}
                }
                addListener.getSummaryPrize(summaryPrize)
            }
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
    }

    private fun updateProductQuantity(productId: String, addedQuantity: Int) = viewModelScope.launch{
        when(val response = productRepository.updateProductQuantity(productId, addedQuantity)){
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
    }

    private fun addSelling(selling: SellingWithoutId) = viewModelScope.launch {
        when(val response = sellingRepository.addSelling(selling)){
            is NetworkResponse.Success -> {
                addListener.addSellingSucceed()
                for(q in sellingQuantities) updateProductQuantity(q.key, -q.value)
                sellingQuantities = mutableMapOf()
            }
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
    }
}