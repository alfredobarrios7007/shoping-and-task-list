package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.fake.FakeShoppingListRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CloneShoppingListUseCaseTest {

    private lateinit var repository: FakeShoppingListRepository
    private lateinit var useCase: CloneShoppingListUseCase

    @Before
    fun setUp() {
        repository = FakeShoppingListRepository()
        useCase = CloneShoppingListUseCase(repository)
    }

    @Test
    fun `clone copies source list and links back via clonedFromListId`() = runTest {
        repository.seedList(
            ShoppingList(
                id = 1,
                name = "Weekly Groceries",
                createdAt = 0,
                updatedAt = 0,
                isRecurringTemplate = false,
                recurrenceInterval = null,
                nextDueAt = null,
                lastGeneratedAt = null,
                clonedFromListId = null
            )
        )

        val newId = useCase(sourceListId = 1, newListName = "Weekly Groceries copy")

        val cloned = repository.getList(newId).first()
        assertNotNull(cloned)
        assertEquals("Weekly Groceries copy", cloned!!.name)
        assertEquals(1L, cloned.clonedFromListId)
        assertFalse(cloned.isRecurringTemplate)
    }

    @Test
    fun `cloning a recurring template produces a plain active list, not another template`() = runTest {
        repository.seedList(
            ShoppingList(
                id = 5,
                name = "Weekly Groceries",
                createdAt = 0,
                updatedAt = 0,
                isRecurringTemplate = true,
                recurrenceInterval = RecurrenceInterval.WEEKLY,
                nextDueAt = 1000L,
                lastGeneratedAt = null,
                clonedFromListId = null
            )
        )

        val newId = useCase(sourceListId = 5, newListName = "Weekly Groceries")

        val generated = repository.getList(newId).first()
        assertNotNull(generated)
        assertFalse(generated!!.isRecurringTemplate)
        assertEquals(5L, generated.clonedFromListId)
    }
}
