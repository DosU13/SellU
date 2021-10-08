package com.dosu.sellu.data.network

import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.model.Product.Companion.toProduct
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.data.network.selling.model.Selling.Companion.toSelling
import com.dosu.sellu.data.network.selling.model.SellingWithoutId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun getProduct(productId: String): Product {
        return db.collection(productsRef).document(productId).get().await().toProduct()!!
    }

    suspend fun addProduct(product: ProductWithoutId): String {
        return db.collection(productsRef).add(product).await().id
    }

    suspend fun updateProductDetails(productId: String, name: String, numOfImages: Long, description: String,
                                     prize: Double, ownPrize: Double, quantity: Long){
        db.collection(productsRef).document(productId).update(
            "name", name, "numOfImages", numOfImages, "description", description,
            "prize", prize, "ownPrize", ownPrize, "quantity", quantity).await()
    }

    suspend fun updateProductQuantity(productId: String, addedQuantity: Int) {
        db.collection(productsRef).document(productId).update("quantity", FieldValue.increment(addedQuantity.toLong())).await()
    }

    suspend fun getSellingList(): List<Selling>{
        return db.collection(sellingRef).orderBy("time", Query.Direction.DESCENDING).get().await().documents.mapNotNull { it.toSelling() }
    }

    suspend fun addSelling(selling: SellingWithoutId){
        db.collection(sellingRef).add(selling).await()
    }

    suspend fun uploadImage(refString: String, byteArray: ByteArray){
        val imgRef = storageRef.child(IMG_REF+refString)
        imgRef.putBytes(byteArray).await()
    }

    suspend fun downloadImage(refString: String): ByteArray{
        val imgRef = storageRef.child(IMG_REF+refString)
        return imgRef.getBytes(MAX_BYTES).await()
    }
}