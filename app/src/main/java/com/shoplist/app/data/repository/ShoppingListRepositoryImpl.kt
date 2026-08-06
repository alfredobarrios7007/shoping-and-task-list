package com.shoplist.app.data.repository

import androidx.room.withTransaction
import com.shoplist.app.data.local.AppDatabase
import com.shoplist.app.data.local.dao.ShoppingListDao
import com.shoplist.app.data.local.dao.ShoppingListItemDao
import com.shoplist.app.data.local.entity.ShoppingListEntity
import com.shoplist.app.data.mapper.toDomain
import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val shoppingListDao: ShoppingListDao,
    private val shoppingListItemDao: ShoppingListItemDao
) : ShoppingListRepository {

    override fun getActiveLists(): Flow<List<ShoppingList>> =
        shoppingListDao.getActiveLists().map { list -> list.map { it.toDomain() } }

    override fun getRecurringTemplates(): Flow<List<ShoppingList>> =
        shoppingListDao.getRecurringTemplates().map { list -> list.map { it.toDomain() } }

    override fun getList(id: Long): Flow<ShoppingList?> =
        shoppingListDao.getList(id).map { it?.toDomain() }

    override suspend fun createList(name: String): Long {
        val now = System.currentTimeMillis()
        return shoppingListDao.insert(ShoppingListEntity(name = name, createdAt = now, updatedAt = now))
    }

    override suspend fun renameList(id: Long, name: String) =
        shoppingListDao.rename(id, name, System.currentTimeMillis())

    override suspend fun deleteList(id: Long) = shoppingListDao.delete(id)

    override suspend fun setRecurring(id: Long, interval: RecurrenceInterval?, nextDueAt: Long?) =
        shoppingListDao.setRecurring(id, interval, nextDueAt)

    override suspend fun getDueTemplates(now: Long): List<ShoppingList> =
        shoppingListDao.getDueTemplates(now).map { entity ->
            ShoppingList(
                id = entity.id,
                name = entity.name,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isRecurringTemplate = entity.isRecurringTemplate,
                recurrenceInterval = entity.recurrenceInterval,
                nextDueAt = entity.nextDueAt,
                lastGeneratedAt = entity.lastGeneratedAt,
                clonedFromListId = entity.clonedFromListId
            )
        }

    override suspend fun cloneList(sourceListId: Long, newName: String): Long =
        appDatabase.withTransaction { copyList(sourceListId, newName) }

    override suspend fun regenerateFromTemplate(
        templateId: Long,
        newListName: String,
        newNextDueAt: Long,
        generatedAt: Long
    ): Long = appDatabase.withTransaction {
        val newListId = copyList(templateId, newListName)
        shoppingListDao.advanceNextDue(templateId, newNextDueAt, generatedAt)
        newListId
    }

    private suspend fun copyList(sourceListId: Long, newName: String): Long {
        val now = System.currentTimeMillis()
        val newListId = shoppingListDao.insert(
            ShoppingListEntity(
                name = newName,
                createdAt = now,
                updatedAt = now,
                clonedFromListId = sourceListId
            )
        )
        val sourceItems = shoppingListItemDao.getItemEntitiesForList(sourceListId)
        if (sourceItems.isNotEmpty()) {
            shoppingListItemDao.insertAll(sourceItems.map { it.copy(id = 0, listId = newListId) })
        }
        return newListId
    }
}
