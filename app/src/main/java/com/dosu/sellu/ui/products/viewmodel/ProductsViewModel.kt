package com.dosu.sellu.ui.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.util.ImageListener
import com.dosu.sellu.ui.products.util.ProductsListener
import kotlinx.coroutines.launch

class ProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private lateinit var productsListener: ProductsListener
    private lateinit var addProductListener: AddProductListener
    private lateinit var imageListener: ImageListener

    fun setListener(productsListener: ProductsListener) {
        this.productsListener = productsListener
    }

    fun setListener(addProductListener: AddProductListener) {
        this.addProductListener = addProductListener
    }

    fun setListener(imageListener: ImageListener){
        this.imageListener = imageListener
    }

    fun getProduct(productId: String) = viewModelScope.launch {
        when(val response = productRepository.getProduct(productId)){
            is NetworkResponse.Success -> addProductListener.getProduct(response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
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

    fun updateProductDetails(productId: String, name: String, numOfImages: Long, description: String,
                             prize: Double, ownPrize: Double, quantity: Long) = viewModelScope.launch{
        when(val response = productRepository.updateProductDetails(productId, name, numOfImages,
                            description, prize, ownPrize, quantity)){
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(name, productId)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun downloadImages(productId: String, numOfImages: Int){
        for(i in 0 until numOfImages){
            downloadImage(productId, i)
        }
    }

    fun downloadImage(productId: String, photoIndex: Int) = viewModelScope.launch {
        when (val response = productRepository.downloadImage(productId, photoIndex)){
            is NetworkResponse.Success -> imageListener.downloadImage(response.value, productId, photoIndex)
            is NetworkResponse.Failure -> imageListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun uploadImages(productId: String, byteArrays: List<ByteArray>){
        for (i in byteArrays.indices){
            uploadImage(productId, i, byteArrays[i])
        }
    }

    private fun uploadImage(productId: String, photoIndex: Int, byteArray: ByteArray) = viewModelScope.launch {
        when(val response = productRepository.uploadImage(productId, photoIndex, byteArray)){
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun todaySold(productId: String): Int {
        return 3
    }
}