package com.shoplist.app.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shoplist.app.data.local.AppDatabase
import com.shoplist.app.data.local.entity.CategoryEntity
import com.shoplist.app.data.local.entity.ProductEntity
import com.shoplist.app.data.local.entity.ShoppingListEntity
import com.shoplist.app.data.local.entity.ShoppingListItemEntity
import com.shoplist.app.domain.model.Priority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingListItemDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var productDao: ProductDao
    private lateinit var shoppingListDao: ShoppingListDao
    private lateinit var itemDao: ShoppingListItemDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        categoryDao = database.categoryDao()
        productDao = database.productDao()
        shoppingListDao = database.shoppingListDao()
        itemDao = database.shoppingListItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getItemsForList_ordersByPriorityThenInsertionSequence() = runTest {
        val produceId = categoryDao.insert(CategoryEntity(name = "Produce", createdAt = 0))
        val dairyId = categoryDao.insert(CategoryEntity(name = "Dairy", createdAt = 0))

        val bananas = productDao.insert(ProductEntity(name = "Bananas", categoryId = produceId, defaultUnit = null, createdAt = 0))
        val apples = productDao.insert(ProductEntity(name = "Apples", categoryId = produceId, defaultUnit = null, createdAt = 0))
        val milk = productDao.insert(ProductEntity(name = "Milk", categoryId = dairyId, defaultUnit = null, createdAt = 0))

        val listId = shoppingListDao.insert(ShoppingListEntity(name = "Groceries", createdAt = 0, updatedAt = 0))

        // Inserted NORMAL, HIGH, LOW (in that order) - priority should win over insertion
        // order, regardless of category/product name.
        itemDao.insert(ShoppingListItemEntity(listId = listId, productId = bananas, quantity = 1.0, unit = null, note = null, priority = Priority.NORMAL, addedAt = 1))
        itemDao.insert(ShoppingListItemEntity(listId = listId, productId = apples, quantity = 1.0, unit = null, note = null, priority = Priority.HIGH, addedAt = 2))
        itemDao.insert(ShoppingListItemEntity(listId = listId, productId = milk, quantity = 1.0, unit = null, note = null, priority = Priority.LOW, addedAt = 3))

        val items = itemDao.getItemsForList(listId).first()

        assertEquals(listOf("Apples", "Bananas", "Milk"), items.map { it.productName })
    }

    @Test
    fun getItemsForList_samePriorityKeepsInsertionSequence() = runTest {
        val produceId = categoryDao.insert(CategoryEntity(name = "Produce", createdAt = 0))
        val bananas = productDao.insert(ProductEntity(name = "Bananas", categoryId = produceId, defaultUnit = null, createdAt = 0))
        val apples = productDao.insert(ProductEntity(name = "Apples", categoryId = produceId, defaultUnit = null, createdAt = 0))

        val listId = shoppingListDao.insert(ShoppingListEntity(name = "Groceries", createdAt = 0, updatedAt = 0))

        // Bananas added before Apples, both NORMAL priority: insertion order should win,
        // not alphabetical order (matches "sequence" ordering like a daily routine list).
        itemDao.insert(ShoppingListItemEntity(listId = listId, productId = bananas, quantity = 1.0, unit = null, note = null, addedAt = 1))
        itemDao.insert(ShoppingListItemEntity(listId = listId, productId = apples, quantity = 1.0, unit = null, note = null, addedAt = 2))

        val items = itemDao.getItemsForList(listId).first()

        assertEquals(listOf("Bananas", "Apples"), items.map { it.productName })
    }

    @Test
    fun getFrequentProducts_excludesTemplateListsAndOrdersByPurchaseCount() = runTest {
        val categoryId = categoryDao.insert(CategoryEntity(name = "Produce", createdAt = 0))
        val apples = productDao.insert(ProductEntity(name = "Apples", categoryId = categoryId, defaultUnit = null, createdAt = 0))
        val bananas = productDao.insert(ProductEntity(name = "Bananas", categoryId = categoryId, defaultUnit = null, createdAt = 0))

        val activeListId = shoppingListDao.insert(ShoppingListEntity(name = "Groceries", createdAt = 0, updatedAt = 0))
        val templateListId = shoppingListDao.insert(
            ShoppingListEntity(name = "Weekly Template", createdAt = 0, updatedAt = 0, isRecurringTemplate = true)
        )

        // Apples purchased twice on the active list, bananas once; an extra apples entry
        // on the template list should NOT count toward purchase history.
        itemDao.insert(ShoppingListItemEntity(listId = activeListId, productId = apples, quantity = 1.0, unit = null, note = null, addedAt = 100))
        itemDao.insert(ShoppingListItemEntity(listId = activeListId, productId = apples, quantity = 1.0, unit = null, note = null, addedAt = 200))
        itemDao.insert(ShoppingListItemEntity(listId = activeListId, productId = bananas, quantity = 1.0, unit = null, note = null, addedAt = 150))
        itemDao.insert(ShoppingListItemEntity(listId = templateListId, productId = apples, quantity = 1.0, unit = null, note = null, addedAt = 500))

        val frequent = productDao.getFrequentProducts(sinceMillis = 0, limit = 10).first()

        assertEquals(listOf("Apples", "Bananas"), frequent.map { it.name })
        assertEquals(2, frequent.first { it.name == "Apples" }.purchaseCount)
    }
}
