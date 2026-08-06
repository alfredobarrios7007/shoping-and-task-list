package com.shoplist.app.domain.model

data class ShoppingListItem(
    val id: Long,
    val listId: Long,
    val productId: Long,
    val productName: String,
    val categoryId: Long,
    val categoryName: String,
    val quantity: Double,
    val unit: String?,
    val note: String?,
    val isChecked: Boolean,
    val priority: Priority,
    val addedAt: Long
)

data class CategoryItemGroup(
    val categoryId: Long,
    val categoryName: String,
    val items: List<ShoppingListItem>
)
