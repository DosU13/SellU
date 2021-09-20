package com.dosu.sellu.data.network.selling.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class Selling(
    val sellingId: String,
    val products: Map<String, Int>,
    val time: Timestamp,
    val prize: Double,
    val newPrize: Double?,
    val newPrizeReason: String?
){
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun DocumentSnapshot.toSelling(): Selling {
            val products: Map<String, Int> = get("products") as Map<String, Int>
            val time = getTimestamp("time")!!
            val prize = getDouble("prize")!!
            val newPrize = getDouble("newPrize")
            val newPrizeReason = getString("newPrizeReason")
            return Selling(id, products, time, prize, newPrize, newPrizeReason)
        }
    }
}