package com.dosu.sellu.ui.selling.viewmodel

import android.text.format.DateUtils
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
import java.util.*

class SellingViewModel(private val sellingRepository: SellingRepository, private val productRepository: ProductRepository) : ViewModel(){
    private lateinit var addListener: AddSellingListener

    private var sellingQuantities = mutableMapOf<String, Int>()

    fun setListener(listener: AddSellingListener){
        addListener = listener
    }

    fun getSellingQuantity(productId: String): Int {
        return if(sellingQuantities.containsKey(productId)) sellingQuantities[productId]!!
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

    private fun updateProductQuantity(productId: String, quantityDecrement: Int) = viewModelScope.launch{
        var product: Product? = null
        when(val response = productRepository.getProduct(productId)){
            is NetworkResponse.Success -> product = response.value
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
        product?.let {
            val response = if (DateUtils.isToday(it.lastSoldDay.time))
                                productRepository.incrementProductLastDaySold(productId, quantityDecrement)
                else productRepository.updateProductLastDaySold(productId, Calendar.getInstance().time, quantityDecrement)
            if (response is NetworkResponse.Failure)
                addListener.anyError(response.errorCode, response.errorBody)
        }
        when(val response = productRepository.incrementProductQuantity(productId, -quantityDecrement)){
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
    }

    private fun addSelling(selling: SellingWithoutId) = viewModelScope.launch {
        when(val response = sellingRepository.addSelling(selling)){
            is NetworkResponse.Success -> {
                addListener.addSellingSucceed()
                for(q in sellingQuantities) updateProductQuantity(q.key, q.value)
                sellingQuantities = mutableMapOf()
            }
            is NetworkResponse.Failure -> addListener.anyError(response.errorCode, response.errorBody)
        }
    }
}