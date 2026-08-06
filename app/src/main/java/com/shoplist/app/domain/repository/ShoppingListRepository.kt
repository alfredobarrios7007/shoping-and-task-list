package com.shoplist.app.domain.repository

import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getActiveLists(): Flow<List<ShoppingList>>
    fun getRecurringTemplates(): Flow<List<ShoppingList>>
    fun getList(id: Long): Flow<ShoppingList?>
    suspend fun createList(name: String): Long
    suspend fun renameList(id: Long, name: String)
    suspend fun deleteList(id: Long)
    suspend fun setRecurring(id: Long, interval: RecurrenceInterval?, nextDueAt: Long?)
    suspend fun getDueTemplates(now: Long): List<ShoppingList>
    suspend fun cloneList(sourceListId: Long, newName: String): Long

    /**
     * Atomically clones [templateId] into a new active list and advances the
     * template's next-due timestamp in a single transaction, so a worker run
     * that's killed mid-way can't leave a template due-but-already-generated
     * (which would otherwise cause a duplicate list on retry).
     */
    suspend fun regenerateFromTemplate(templateId: Long, newListName: String, newNextDueAt: Long, generatedAt: Long): Long
}
