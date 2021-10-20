package com.dosu.sellu.data.network.product.model

import java.util.*

data class ProductWithoutId(
    val name: String,
    val thumbnail: String?,
    val images: List<String?>,
    val description: String?,
    val prize: Double,
    val quantity: Long,
    val ownPrize: Double,
    val todayDate: Date,
    val todaySold: Long
)