package com.dosu.sellu.data.network.product

import android.util.Log
import com.dosu.sellu.data.network.FirebaseService
import com.dosu.sellu.data.network.NetworkResponse
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.util.BaseRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ProductRepository(private val firebaseService: FirebaseService): BaseRepository() {
    private val userId get() = FirebaseAuth.getInstance().currentUser!!.uid

    suspend fun getProducts(): NetworkResponse<List<Product>>  = safeApiCall{
        firebaseService.getProducts()
    }

    suspend fun getProduct(productId: String) = safeApiCall{
        firebaseService.getProduct(productId)
    }

    suspend fun addProduct(product: ProductWithoutId) = safeApiCall {
        firebaseService.addProduct(product)
    }

    suspend fun updateProductFields(productId: String, field: String, value: Any?, vararg moreFieldsAndValues: Any?) = safeApiCall{
        firebaseService.updateProductFields(productId, field, value, *moreFieldsAndValues)
    }

    suspend fun updateProductTodaySold(productId: String, todayDate: Date, todaySold: Int) = safeApiCall {
        firebaseService.updateProductTodaySold(productId, todayDate, todaySold.toLong())
    }
    suspend fun incrementProductTodaySold(productId: String, increment:Int) = safeApiCall {
        firebaseService.incrementProductTodaySold(productId, increment.toLong())
    }

    suspend fun incrementProductQuantity(productId: String, addedQuantity: Int) = safeApiCall {
        firebaseService.updateProductQuantity(productId, addedQuantity.toLong())
    }

    suspend fun downloadImage(productId: String, photoIndex: Int) = safeApiCall {
        val refString = "$PRODUCTS/$productId/$photoIndex.jpg"
        firebaseService.downloadImage(refString)
    }

    suspend fun uploadThumbnail(productId: String, byteArray: ByteArray) = safeApiCall {
        val refString = "$PRODUCTS/$productId/thumbnail.jpg"
        firebaseService.uploadImage(refString, byteArray)
    }

    suspend fun uploadImage(productId: String, photoIndex: Int, byteArray: ByteArray) = safeApiCall {
        val refString = "$PRODUCTS/$productId/$photoIndex.jpg"
        firebaseService.uploadImage(refString, byteArray)
    }

    suspend fun getImageDownloadUri(productId: String, photoIndex: Int) = safeApiCall {
        val refString = "$PRODUCTS/$productId/$photoIndex.jpg"
        firebaseService.getImageDownloadUri(refString)
    }

//    suspend fun update() = safeApiCall{
//        firebaseService.update()
//    }

    companion object {
        const val PRODUCTS = "products"
    }
}