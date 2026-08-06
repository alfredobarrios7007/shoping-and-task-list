package com.shoplist.app.domain.model

data class Product(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val categoryName: String,
    val defaultUnit: String?,
    val createdAt: Long
)
