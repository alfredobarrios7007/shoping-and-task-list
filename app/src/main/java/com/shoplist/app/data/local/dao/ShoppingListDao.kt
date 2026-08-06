package com.shoplist.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shoplist.app.data.local.entity.ShoppingListEntity
import com.shoplist.app.domain.model.RecurrenceInterval
import kotlinx.coroutines.flow.Flow

data class ShoppingListWithCountsRow(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isRecurringTemplate: Boolean,
    val recurrenceInterval: RecurrenceInterval?,
    val nextDueAt: Long?,
    val lastGeneratedAt: Long?,
    val clonedFromListId: Long?,
    val itemCount: Int,
    val checkedItemCount: Int
)

private const val LIST_WITH_COUNTS_SELECT = """
    SELECT l.id AS id, l.name AS name, l.createdAt AS createdAt, l.updatedAt AS updatedAt,
           l.isRecurringTemplate AS isRecurringTemplate, l.recurrenceInterval AS recurrenceInterval,
           l.nextDueAt AS nextDueAt, l.lastGeneratedAt AS lastGeneratedAt, l.clonedFromListId AS clonedFromListId,
           COUNT(i.id) AS itemCount,
           SUM(CASE WHEN i.isChecked THEN 1 ELSE 0 END) AS checkedItemCount
    FROM shopping_list l
    LEFT JOIN shopping_list_item i ON i.listId = l.id
"""

@Dao
interface ShoppingListDao {
    @Query(
        "$LIST_WITH_COUNTS_SELECT WHERE l.isRecurringTemplate = 0 GROUP BY l.id ORDER BY l.updatedAt DESC"
    )
    fun getActiveLists(): Flow<List<ShoppingListWithCountsRow>>

    @Query(
        "$LIST_WITH_COUNTS_SELECT WHERE l.isRecurringTemplate = 1 GROUP BY l.id ORDER BY l.name"
    )
    fun getRecurringTemplates(): Flow<List<ShoppingListWithCountsRow>>

    @Query(
        "$LIST_WITH_COUNTS_SELECT WHERE l.id = :id GROUP BY l.id"
    )
    fun getList(id: Long): Flow<ShoppingListWithCountsRow?>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    suspend fun getListEntity(id: Long): ShoppingListEntity?

    @Query("SELECT * FROM shopping_list WHERE isRecurringTemplate = 1 AND nextDueAt <= :now")
    suspend fun getDueTemplates(now: Long): List<ShoppingListEntity>

    @Insert
    suspend fun insert(list: ShoppingListEntity): Long

    @Query("UPDATE shopping_list SET name = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun rename(id: Long, name: String, updatedAt: Long)

    @Query("DELETE FROM shopping_list WHERE id = :id")
    suspend fun delete(id: Long)

    @Query(
        """
        UPDATE shopping_list
        SET isRecurringTemplate = (:interval IS NOT NULL), recurrenceInterval = :interval, nextDueAt = :nextDueAt
        WHERE id = :id
        """
    )
    suspend fun setRecurring(id: Long, interval: RecurrenceInterval?, nextDueAt: Long?)

    @Query("UPDATE shopping_list SET nextDueAt = :newNextDueAt, lastGeneratedAt = :generatedAt WHERE id = :id")
    suspend fun advanceNextDue(id: Long, newNextDueAt: Long, generatedAt: Long)
}
