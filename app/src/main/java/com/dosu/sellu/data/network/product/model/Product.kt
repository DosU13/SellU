package com.dosu.sellu.data.network.product.model

import android.util.Log
import com.google.common.util.concurrent.UncheckedExecutionException
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

data class Product(
    val productId: String,
    val name: String,
    val numOfImages: Long,
    val description: String?,
    val prize: Double,
    val quantity: Long
){
    companion object{
        @Suppress("UNCHECKED_CAST")
        fun DocumentSnapshot.toProduct(): Product? {
            return try {
                val name = getString("name")!!
                val numOfImages = getLong("numOfImages")!!
                val description = getString("description")
                val prize = String.format("%.2f", getDouble("prize")!!).toDouble()
                val quantity = getLong("quantity")!!
                Product(id, name, numOfImages, description, prize, quantity)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting product", e)
//                FirebaseCrashlytics.getInstance().log("Error converting user profile")
//                FirebaseCrashlytics.getInstance().serCustomKey("userId", id)
//                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Product"
    }
}