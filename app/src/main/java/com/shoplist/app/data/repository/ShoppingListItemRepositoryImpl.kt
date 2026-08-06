package com.shoplist.app.data.repository

import com.shoplist.app.data.local.dao.ShoppingListItemDao
import com.shoplist.app.data.local.entity.ShoppingListItemEntity
import com.shoplist.app.data.mapper.toDomain
import com.shoplist.app.domain.model.Priority
import com.shoplist.app.domain.model.ShoppingListItem
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListItemRepositoryImpl @Inject constructor(
    private val itemDao: ShoppingListItemDao
) : ShoppingListItemRepository {

    override fun getItemsForList(listId: Long): Flow<List<ShoppingListItem>> =
        itemDao.getItemsForList(listId).map { list -> list.map { it.toDomain() } }

    override suspend fun addItem(
        listId: Long,
        productId: Long,
        quantity: Double,
        unit: String?,
        note: String?,
        priority: Priority
    ): Long = itemDao.insert(
        ShoppingListItemEntity(
            listId = listId,
            productId = productId,
            quantity = quantity,
            unit = unit,
            note = note,
            priority = priority,
            addedAt = System.currentTimeMillis()
        )
    )

    override suspend fun updateItem(item: ShoppingListItem) =
        itemDao.update(
            ShoppingListItemEntity(
                id = item.id,
                listId = item.listId,
                productId = item.productId,
                quantity = item.quantity,
                unit = item.unit,
                note = item.note,
                isChecked = item.isChecked,
                priority = item.priority,
                addedAt = item.addedAt
            )
        )

    override suspend fun setChecked(itemId: Long, checked: Boolean) = itemDao.setChecked(itemId, checked)

    override suspend fun removeItem(itemId: Long) = itemDao.delete(itemId)
}
