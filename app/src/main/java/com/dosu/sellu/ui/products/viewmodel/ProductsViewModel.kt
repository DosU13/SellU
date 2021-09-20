package com.dosu.sellu.ui.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.util.ProductsListener
import kotlinx.coroutines.launch

class ProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private lateinit var productsListener: ProductsListener
    private lateinit var addProductListener: AddProductListener

    fun setListener(productsListener: ProductsListener) {
        this.productsListener = productsListener
    }

    fun setListener(addProductListener: AddProductListener) {
        this.addProductListener = addProductListener
    }

    fun uploadImages(productId: String, byteArrays: List<ByteArray>){
        for (i in byteArrays.indices){
            uploadImage(productId, i, byteArrays[i])
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

    fun addProduct(product: ProductWithoutId) = viewModelScope.launch {
        when (val response = productRepository.addProduct(product)) {
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(product.name, response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(
                response.errorCode,
                response.errorBody
            )
        }
    }

    fun downloadImage(productId: String, photoIndex: Int) = viewModelScope.launch {
        when (val response = productRepository.downloadImage(productId, photoIndex)){
            is NetworkResponse.Success -> productsListener.downloadImage(response.value, productId, photoIndex)
            is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
        }
    }

    private fun uploadImage(productId: String, photoIndex: Int, byteArray: ByteArray) = viewModelScope.launch {
        when(val response = productRepository.uploadImage(productId, photoIndex, byteArray)){
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }
}