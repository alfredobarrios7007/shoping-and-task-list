package com.shoplist.app.domain.model

data class FrequentProduct(
    val productId: Long,
    val name: String,
    val categoryId: Long,
    val defaultUnit: String?,
    val purchaseCount: Int,
    val lastAddedAt: Long
)
