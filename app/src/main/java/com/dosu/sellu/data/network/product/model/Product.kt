package com.dosu.sellu.data.network.product.model

import android.text.format.DateUtils
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Product(
    val productId: String,
    val name: String,
    val thumbnail: String?,
    val images: List<String?>,
    val description: String?,
    val prize: Double,
    val quantity: Long,
    val ownPrize: Double,
    val lastSoldDay: Date,
    val lastDaySold: Long
){
    companion object{
        @Suppress("UNCHECKED_CAST")
        fun DocumentSnapshot.toProduct(): Product? {
            return try {
                val name = getString("name")!!
                val description = getString("description")
                val thumbnail = getString("thumbnail")
                val images = get("images") as List<String?>
                val prize = String.format("%.2f", getDouble("prize")!!).toDouble()
                val quantity = getLong("quantity")!!
                val ownPrize = String.format("%.2f", getDouble("ownPrize")!!).toDouble()
                val lastSoldDay = getDate("todayDate")!!
                val lastDaySold = if(DateUtils.isToday(lastSoldDay.time)) getLong("todaySold")!! else 0
                Product(id, name, thumbnail, images, description, prize, quantity
                    ,ownPrize, lastSoldDay, lastDaySold)
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