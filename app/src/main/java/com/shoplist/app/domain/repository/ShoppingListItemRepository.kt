package com.shoplist.app.domain.repository

import com.shoplist.app.domain.model.Priority
import com.shoplist.app.domain.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

interface ShoppingListItemRepository {
    fun getItemsForList(listId: Long): Flow<List<ShoppingListItem>>
    suspend fun addItem(
        listId: Long,
        productId: Long,
        quantity: Double,
        unit: String?,
        note: String?,
        priority: Priority = Priority.NORMAL
    ): Long
    suspend fun updateItem(item: ShoppingListItem)
    suspend fun setChecked(itemId: Long, checked: Boolean)
    suspend fun removeItem(itemId: Long)
}
