package com.dosu.sellu.data.network.product.model

data class ProductWithoutId(
    val name: String,
    val numOfImages: Long,
    val description: String?,
    val prize: Double,
    val quantity: Long
)