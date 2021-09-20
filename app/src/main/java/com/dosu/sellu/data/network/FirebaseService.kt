package com.dosu.sellu.data.network

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.model.Product.Companion.toProduct
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.data.network.selling.model.Selling.Companion.toSelling
import com.dosu.sellu.data.network.selling.model.SellingWithoutId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val userId get() = FirebaseAuth.getInstance().currentUser!!.uid
    private val storageRef = Firebase.storage.reference
    private val productsRef get() = "users/${userId}/products"
    private val sellingRef get() = "users/${userId}/selling"

    companion object {
        private const val TAG = "FirebaseService"
        private const val IMG_REF = "images/"
        private const val MAX_BYTES: Long = 10*1024*1024
    }

    suspend fun getProducts(): List<Product> {
        return db.collection(productsRef).get().await().documents.mapNotNull { it.toProduct()}
    }

    suspend fun addProduct(product: ProductWithoutId): String {
        return db.collection(productsRef).add(product).await().id
    }

    suspend fun getSelling(): List<Selling>{
        return db.collection(sellingRef).get().await().documents.mapNotNull { it.toSelling() }
    }

    fun addSelling(selling: SellingWithoutId){
        db.collection(sellingRef).add(selling)
    }

    suspend fun updateProductQuantity(productId: String, newQuantityMinus: Int) {
        val oldQuantity: Long = db.collection(productsRef).document(productId).get().await().toProduct()!!.quantity
        db.collection(productsRef).document(productId).update("quantity", oldQuantity-newQuantityMinus)
    }

    fun uploadImage(refString: String, byteArray: ByteArray){
        val imgRef = storageRef.child(IMG_REF+refString)
        imgRef.putBytes(byteArray)
    }

    suspend fun downloadImage(refString: String): ByteArray{
        val imgRef = storageRef.child(IMG_REF+refString)
        return imgRef.getBytes(MAX_BYTES).await()
    }
}