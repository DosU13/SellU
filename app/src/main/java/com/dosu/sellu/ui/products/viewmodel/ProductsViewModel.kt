package com.dosu.sellu.ui.products.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.data.network.product.ProductRepository
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.util.ImageListener
import com.dosu.sellu.ui.products.util.ProductsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

    fun getProduct(productId: String) = viewModelScope.launch(Dispatchers.IO) {
        when(val response = productRepository.getProduct(productId)){
            is NetworkResponse.Success -> addProductListener.getProduct(response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun getProducts() = viewModelScope.launch(Dispatchers.IO) {
        when (val response = productRepository.getProducts()) {
            is NetworkResponse.Success -> productsListener.getProducts(response.value)
            is NetworkResponse.Failure -> productsListener.anyError(
                response.errorCode,
                response.errorBody
            )
        }
    }

    fun todaySold(product: Product): Int {
        return if(DateUtils.isToday(product.todayDate.time)){
            product.todaySold.toInt()
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                when(val response = productRepository
                    .updateProductTodaySold(product.productId, Calendar.getInstance().time, 0)){
                    is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
                }
            }
            0
        }
    }

    fun addProduct(product: ProductWithoutId) = viewModelScope.launch(Dispatchers.IO) {
        when (val response = productRepository.addProduct(product)) {
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(product.name, response.value)
            is NetworkResponse.Failure -> addProductListener.anyError(
                response.errorCode,
                response.errorBody
            )
        }
    }

    fun updateProductDetails(productId: String, name: String, numOfImages: Long, description: String,
                             prize: Double, ownPrize: Double, quantity: Long) = viewModelScope.launch(Dispatchers.IO){
        when(val response = productRepository.updateProductDetails(productId, name, numOfImages,
                            description, prize, ownPrize, quantity)){
            is NetworkResponse.Success -> addProductListener.addProductSucceeded(name, productId)
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun updateProductQuantity(productId: String, addedQuantity: Int) = viewModelScope.launch(Dispatchers.IO) {
        when(val response = productRepository.incrementProductQuantity(productId, addedQuantity)){
            is NetworkResponse.Success -> productsListener.updateProductSucceed()
            is NetworkResponse.Failure -> productsListener.anyError(response.errorCode, response.errorBody)
        }
    }

    fun downloadImages(product: Product, productPos: Int) = viewModelScope.launch(Dispatchers.IO) {
        val responses = Array(product.numOfImages.toInt()){ i ->
            productRepository.downloadImage(product.productId, i)
        }
        val failure = responses.find { it is NetworkResponse.Failure } as NetworkResponse.Failure?
        if(failure!=null){
            imageListener.anyError(failure.errorCode, failure.errorBody)
        }else{
            val images = Array(responses.size){(responses[it] as NetworkResponse.Success).value}
            imageListener.downloadImages(images, productPos)
        }
    }

    fun downloadImage(productId: String, photoIndex: Int) = viewModelScope.launch(Dispatchers.IO) {
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

    private fun uploadImage(productId: String, photoIndex: Int, byteArray: ByteArray) = viewModelScope.launch(Dispatchers.IO) {
        when(val response = productRepository.uploadImage(productId, photoIndex, byteArray)){
            is NetworkResponse.Failure -> addProductListener.anyError(response.errorCode, response.errorBody)
        }
    }
}