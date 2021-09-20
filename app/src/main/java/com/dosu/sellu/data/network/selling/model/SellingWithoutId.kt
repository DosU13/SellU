package com.dosu.sellu.data.network.selling.model

import com.google.firebase.Timestamp

data class SellingWithoutId (
    val products: Map<String, Int>,
    val time: Timestamp,
    val prize: Double,
    val newPrize: Double?,
    val newPrizeReason: String?
)