package com.shoplist.app.data.mapper

import com.shoplist.app.data.local.dao.FrequentProductRow
import com.shoplist.app.data.local.dao.ProductWithCategoryRow
import com.shoplist.app.data.local.dao.ShoppingListItemDetailRow
import com.shoplist.app.data.local.dao.ShoppingListWithCountsRow
import com.shoplist.app.data.local.entity.CategoryEntity
import com.shoplist.app.domain.model.Category
import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.model.ShoppingListItem

fun CategoryEntity.toDomain() = Category(id = id, name = name, createdAt = createdAt)

fun ProductWithCategoryRow.toDomain() = Product(
    id = id,
    name = name,
    categoryId = categoryId,
    categoryName = categoryName,
    defaultUnit = defaultUnit,
    createdAt = createdAt
)

fun FrequentProductRow.toDomain() = FrequentProduct(
    productId = productId,
    name = name,
    categoryId = categoryId,
    defaultUnit = defaultUnit,
    purchaseCount = purchaseCount,
    lastAddedAt = lastAddedAt
)

fun ShoppingListWithCountsRow.toDomain() = ShoppingList(
    id = id,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isRecurringTemplate = isRecurringTemplate,
    recurrenceInterval = recurrenceInterval,
    nextDueAt = nextDueAt,
    lastGeneratedAt = lastGeneratedAt,
    clonedFromListId = clonedFromListId,
    itemCount = itemCount,
    checkedItemCount = checkedItemCount
)

fun ShoppingListItemDetailRow.toDomain() = ShoppingListItem(
    id = id,
    listId = listId,
    productId = productId,
    productName = productName,
    categoryId = categoryId,
    categoryName = categoryName,
    quantity = quantity,
    unit = unit,
    note = note,
    isChecked = isChecked,
    priority = priority,
    addedAt = addedAt
)
