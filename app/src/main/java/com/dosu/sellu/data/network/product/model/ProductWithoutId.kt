package com.dosu.sellu.data.network.product.model

import java.util.*

data class ProductWithoutId(
    val name: String,
    val numOfImages: Long,
    val description: String?,
    val prize: Double,
    val quantity: Long,
    val history: Map<Date, Long>,
    val ownPrize: Double,
    val todayDate: Date,
    val todaySold: Long
)