package com.dosu.sellu.ui.products.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.util.ProductsListener
import kotlinx.coroutines.launch
import java.util.*

class ProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private lateinit var productsListener: ProductsListener
    private lateinit var addProductListener: AddProductListener

    fun setListener(productsListener: ProductsListener) {
        this.productsListener = productsListener
    }

    fun setListener(addProductListener: AddProductListener) {
        this.addProductListener = addProductListener
    }

    fun getProduct(productId: String) = viewModelScope.launch {
        when(val response = productRepository.getProduct(productId)){
            is NetworkResponse.Success -> addProductListener.getProduct(response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun getProduct(productId: String, productPos: Int) = viewModelScope.launch {
        when(val response = productRepository.getProduct(productId)){
            is NetworkResponse.Success -> productsListener.getProduct(response.value, productPos)
            is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun getProducts() = viewModelScope.launch {
        when (val response = productRepository.getProducts()) {
            is NetworkResponse.Success -> productsListener.getProducts(response.value)
            is NetworkResponse.Failure -> productsListener.anyError(
                response.errorCode,
                response.errorBody
            )
        }
    }

    fun todaySold(product: Product): Int {
        return if(DateUtils.isToday(product.lastSoldDay.time)){
            product.lastDaySold.toInt()
        }else{
            viewModelScope.launch {
                when(val response = productRepository
                    .updateProductLastDaySold(product.productId, Calendar.getInstance().time, 0)){
                    is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
                }
            }
            0
        }
    }

    fun addProduct(product: ProductWithoutId) = viewModelScope.launch {
        when (val response = productRepository.addProduct(product)) {
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(product.name, response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(
                response.errorCode,
                response.errorBody
            )
        }
    }

    fun updateProductDetails(productId: String, name: String, description: String,
                             prize: Double, ownPrize: Double, quantity: Long) = viewModelScope.launch{
        when(val response = productRepository.updateProductFields(
            productId, "name", name, "description", description,
            "prize", prize, "ownPrize", ownPrize, "quantity", quantity)){
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(name, productId)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun updateProductQuantity(productId: String, addedQuantity: Int, position: Int) = viewModelScope.launch {
        when(val response = productRepository.incrementProductQuantity(productId, addedQuantity)){
            is NetworkResponse.Success -> productsListener.updateProductSucceed(position, response.value)
            is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun uploadThumbnail(productId: String, byteArray: ByteArray) = viewModelScope.launch{
        when(val responseUri = productRepository.uploadThumbnail(productId, byteArray)){
            is NetworkResponse.Success -> {
                when(val response = productRepository.updateProductFields(productId, "thumbnail", responseUri.value.toString())){
                    is NetworkResponse.Success -> addProductListener.thumbnailSucceed()
                    is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
                }
            }
            is NetworkResponse.Failure -> addProductListener.anyError(responseUri.errorCode, responseUri.errorBody)
        }
    }

    fun uploadImages(productId: String, imagesUriOrByteArr: List<Any>) = viewModelScope.launch{
        val uris: MutableList<String?> = MutableList(imagesUriOrByteArr.size){null}
        for ((i, image) in imagesUriOrByteArr.withIndex()){
            when(image){
                is String -> uris[i] = image
                is ByteArray ->
                    when(val response = productRepository.uploadImage(productId, i, image)){
                        is NetworkResponse.Success -> uris[i] = response.value.toString()
                        is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
                    }
            }
        }
        when(val response = productRepository.updateProductFields(productId, "images", uris as List<String?>)){
            is NetworkResponse.Success -> addProductListener.imagesSucceed()
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

//    fun update() = viewModelScope.launch{
//        when(productRepository.update()){
//            is NetworkResponse.Success -> productsListener.anyError(null, ErrorResponse("Success"))
//            is NetworkResponse.Failure -> productsListener.anyError(null, ErrorResponse("Fail"))
//        }
//    }
}