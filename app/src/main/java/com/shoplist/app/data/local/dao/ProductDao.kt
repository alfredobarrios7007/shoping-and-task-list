package com.shoplist.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shoplist.app.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

data class ProductWithCategoryRow(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val categoryName: String,
    val defaultUnit: String?,
    val createdAt: Long
)

data class FrequentProductRow(
    val productId: Long,
    val name: String,
    val categoryId: Long,
    val defaultUnit: String?,
    val purchaseCount: Int,
    val lastAddedAt: Long
)

@Dao
interface ProductDao {
    @Query(
        """
        SELECT p.id AS id, p.name AS name, p.categoryId AS categoryId, c.name AS categoryName,
               p.defaultUnit AS defaultUnit, p.createdAt AS createdAt
        FROM product p
        JOIN category c ON c.id = p.categoryId
        ORDER BY c.name, p.name
        """
    )
    fun getAllProducts(): Flow<List<ProductWithCategoryRow>>

    @Query(
        """
        SELECT p.id AS id, p.name AS name, p.categoryId AS categoryId, c.name AS categoryName,
               p.defaultUnit AS defaultUnit, p.createdAt AS createdAt
        FROM product p
        JOIN category c ON c.id = p.categoryId
        WHERE p.categoryId = :categoryId
        ORDER BY p.name
        """
    )
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductWithCategoryRow>>

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun delete(id: Long)

    @Query(
        """
        SELECT p.id AS productId, p.name AS name, p.categoryId AS categoryId, p.defaultUnit AS defaultUnit,
               COUNT(*) AS purchaseCount, MAX(i.addedAt) AS lastAddedAt
        FROM shopping_list_item i
        JOIN product p ON p.id = i.productId
        JOIN shopping_list l ON l.id = i.listId
        WHERE l.isRecurringTemplate = 0 AND i.addedAt >= :sinceMillis
        GROUP BY p.id
        ORDER BY purchaseCount DESC, lastAddedAt DESC
        LIMIT :limit
        """
    )
    fun getFrequentProducts(sinceMillis: Long, limit: Int): Flow<List<FrequentProductRow>>
}
