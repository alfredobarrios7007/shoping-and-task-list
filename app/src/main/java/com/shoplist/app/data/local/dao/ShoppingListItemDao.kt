package com.shoplist.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shoplist.app.data.local.entity.ShoppingListItemEntity
import com.shoplist.app.domain.model.Priority
import kotlinx.coroutines.flow.Flow

data class ShoppingListItemDetailRow(
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

@Dao
interface ShoppingListItemDao {
    @Query(
        """
        SELECT i.id AS id, i.listId AS listId, i.productId AS productId, p.name AS productName,
               p.categoryId AS categoryId, c.name AS categoryName, i.quantity AS quantity,
               i.unit AS unit, i.note AS note, i.isChecked AS isChecked, i.priority AS priority,
               i.addedAt AS addedAt
        FROM shopping_list_item i
        JOIN product p ON p.id = i.productId
        JOIN category c ON c.id = p.categoryId
        WHERE i.listId = :listId
        ORDER BY CASE i.priority WHEN 'HIGH' THEN 0 WHEN 'NORMAL' THEN 1 ELSE 2 END, i.id
        """
    )
    fun getItemsForList(listId: Long): Flow<List<ShoppingListItemDetailRow>>

    @Query("SELECT * FROM shopping_list_item WHERE listId = :listId")
    suspend fun getItemEntitiesForList(listId: Long): List<ShoppingListItemEntity>

    @Insert
    suspend fun insert(item: ShoppingListItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<ShoppingListItemEntity>)

    @Update
    suspend fun update(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list_item SET isChecked = :checked WHERE id = :itemId")
    suspend fun setChecked(itemId: Long, checked: Boolean)

    @Query("DELETE FROM shopping_list_item WHERE id = :itemId")
    suspend fun delete(itemId: Long)
}
