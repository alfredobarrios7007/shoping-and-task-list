package com.shoplist.app.fake

import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeShoppingListRepository : ShoppingListRepository {
    private val lists = mutableMapOf<Long, ShoppingList>()
    private var nextId = 1L

    fun seedList(list: ShoppingList) {
        lists[list.id] = list
        if (list.id >= nextId) nextId = list.id + 1
    }

    fun getSeeded(id: Long): ShoppingList? = lists[id]

    override fun getActiveLists(): Flow<List<ShoppingList>> =
        flowOf(lists.values.filter { !it.isRecurringTemplate })

    override fun getRecurringTemplates(): Flow<List<ShoppingList>> =
        flowOf(lists.values.filter { it.isRecurringTemplate })

    override fun getList(id: Long): Flow<ShoppingList?> = flowOf(lists[id])

    override suspend fun createList(name: String): Long {
        val id = nextId++
        lists[id] = ShoppingList(
            id = id,
            name = name,
            createdAt = 0,
            updatedAt = 0,
            isRecurringTemplate = false,
            recurrenceInterval = null,
            nextDueAt = null,
            lastGeneratedAt = null,
            clonedFromListId = null
        )
        return id
    }

    override suspend fun renameList(id: Long, name: String) {
        lists[id]?.let { lists[id] = it.copy(name = name) }
    }

    override suspend fun deleteList(id: Long) {
        lists.remove(id)
    }

    override suspend fun setRecurring(id: Long, interval: RecurrenceInterval?, nextDueAt: Long?) {
        lists[id]?.let {
            lists[id] = it.copy(
                isRecurringTemplate = interval != null,
                recurrenceInterval = interval,
                nextDueAt = nextDueAt
            )
        }
    }

    override suspend fun getDueTemplates(now: Long): List<ShoppingList> =
        lists.values.filter { it.isRecurringTemplate && (it.nextDueAt ?: Long.MAX_VALUE) <= now }

    override suspend fun cloneList(sourceListId: Long, newName: String): Long {
        require(lists.containsKey(sourceListId)) { "Source list $sourceListId not found" }
        val id = nextId++
        lists[id] = ShoppingList(
            id = id,
            name = newName,
            createdAt = 0,
            updatedAt = 0,
            isRecurringTemplate = false,
            recurrenceInterval = null,
            nextDueAt = null,
            lastGeneratedAt = null,
            clonedFromListId = sourceListId
        )
        return id
    }

    override suspend fun regenerateFromTemplate(
        templateId: Long,
        newListName: String,
        newNextDueAt: Long,
        generatedAt: Long
    ): Long {
        val newId = cloneList(templateId, newListName)
        lists[templateId]?.let {
            lists[templateId] = it.copy(nextDueAt = newNextDueAt, lastGeneratedAt = generatedAt)
        }
        return newId
    }
}
