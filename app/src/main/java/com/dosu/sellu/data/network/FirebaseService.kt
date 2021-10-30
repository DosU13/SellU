package com.dosu.sellu.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull
import com.dosu.sellu.data.network.product.ProductRepository
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
import java.io.ByteArrayOutputStream
import java.util.*

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

    suspend fun updateProductFields(productId: String, field: String, value: Any?, vararg moreFieldsAndValues: Any?){
        db.collection(productsRef).document(productId).update(field, value, *moreFieldsAndValues).await()
    }

    suspend fun updateProductTodaySold(productId: String, todayDate: Date, todaySold: Long){
        db.collection(productsRef).document(productId).update(
            "todayDate", todayDate, "todaySold", todaySold).await()
    }
    suspend fun incrementProductTodaySold(productId: String, increment: Long){
        db.collection(productsRef).document(productId).update("todaySold", FieldValue.increment(increment)).await()
    }

    suspend fun updateProductQuantity(productId: String, addedQuantity: Long): Product{
        db.collection(productsRef).document(productId).update("quantity", FieldValue.increment(addedQuantity)).await()
        return getProduct(productId)
    }

    suspend fun getSellingList(): List<Selling>{
        return db.collection(sellingRef).orderBy("time", Query.Direction.DESCENDING).get().await()
            .documents.mapNotNull { it.toSelling() }
    }

    suspend fun addSelling(selling: SellingWithoutId){
        db.collection(sellingRef).add(selling).await()
    }

    suspend fun uploadImage(refString: String, byteArray: ByteArray): Uri{
        val imgRef = storageRef.child(IMG_REF+refString)
        return imgRef.putBytes(byteArray).await().storage.downloadUrl.await()
    }

    suspend fun downloadImage(refString: String): ByteArray{
        val imgRef = storageRef.child(IMG_REF+refString)
        return imgRef.getBytes(MAX_BYTES).await()
    }

    suspend fun getImageDownloadUri(refString: String): Uri{
        val imgRef = storageRef.child(IMG_REF+refString)
        return imgRef.downloadUrl.await()
    }

    suspend fun update() {
        Log.e("Update", "Start")
        val products = db.collection(productsRef).get().await().documents.mapNotNull { it.toProduct()}
        Log.e("Update", "Here$products")
        for(p in products){
            p.ownPrize
        }
    }
}

